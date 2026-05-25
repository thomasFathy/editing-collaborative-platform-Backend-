package ntg.documentation.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.*;
import ntg.documentation.example.domain.dto.CursorMessage;
import ntg.documentation.example.domain.dto.DocumentSyncMessage;
import ntg.documentation.example.domain.dto.EditOperationMessage;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.domain.entity.DocumentOperation;
import ntg.documentation.example.repository.DocumentContentRepository;
import ntg.documentation.example.repository.DocumentOperationRepository;
import ntg.documentation.example.repository.DocumentRepository;
import ntg.documentation.example.service.CollaborationService;
import ntg.documentation.example.service.DocumentContentService;
import ntg.documentation.example.service.PermissionService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Getter
@Setter
public class CollaborationServiceImpl implements CollaborationService {

    private final DocumentOperationRepository operationRepo;
    private final DocumentContentRepository contentRepo;
    private final DocumentRepository documentRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final PermissionService permissionService;
    private final DocumentContentService contentService;

    @Override
    @Transactional
    public void processOperation(UUID docId, UUID userId, EditOperationMessage msg) {
        // Verify document structure state context exists
        documentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document metadata record not found"));

        // 🔥 FIX 1: Use the clean getter on your service layer rather than sending over the whole operation message
        DocumentContent documentContent = contentService.getContent(docId);
        if (documentContent == null) {
            throw new RuntimeException("Document content container context missing");
        }

        String currentContent = documentContent.getContent() != null ? documentContent.getContent() : "";

        // 🔥 FIX 2: Use your dedicated 'applyOperation' helper method below instead of risky manual text splitting
        String result = applyOperation(currentContent, msg);

        // Update the managed state entity values cleanly
        documentContent.setContent(result);
        contentRepo.save(documentContent);

        // 🔥 FIX 3: Broadcast out text payloads instantly directly on the topic Angular is listening to
        messagingTemplate.convertAndSend("/topic/doc/" + docId, msg);
    }

    @Override
    public void handleCursor(UUID docId, CursorMessage msg) {
        messagingTemplate.convertAndSend(
                "/topic/doc/" + docId + "/cursor",
                msg
        );
    }

    private String applyOperation(String content, EditOperationMessage msg) {
        StringBuilder sb = new StringBuilder(content);
        int pos = msg.getPosition();

        if (pos < 0) pos = 0;
        if (pos > sb.length()) pos = sb.length();

        switch (msg.getType()) {
            case INSERT:
                if (msg.getCharacter() != null) {
                    sb.insert(pos, msg.getCharacter());
                }
                break;

            case DELETE:
                if (!sb.isEmpty() && pos < sb.length()) {
                    sb.deleteCharAt(pos);
                }
                break;
        }

        return sb.toString();
    }

    @Override
    public DocumentContent getDocumentContent(UUID docId) {
        // 🔥 FIX 4: Call your custom service layer getter interface target matching signature design rules
        return contentService.getContent(docId);
    }

    @Override
    public void broadcastToDoc(UUID docId, Object payload) {
        messagingTemplate.convertAndSend("/topic/doc/" + docId, payload);
    }

    @Override
    public void syncDocument(UUID docId, DocumentSyncMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/doc/" + docId,
                message
        );
    }

    private DocumentOperation.OperationType mapType(DocumentOperation.OperationType type) {
        return type == DocumentOperation.OperationType.INSERT
                ? DocumentOperation.OperationType.INSERT
                : DocumentOperation.OperationType.DELETE;
    }
}