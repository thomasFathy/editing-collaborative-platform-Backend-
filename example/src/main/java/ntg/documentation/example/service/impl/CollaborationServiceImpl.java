package ntg.documentation.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.EditOperationMessage;
import ntg.documentation.example.domain.dto.OperationBroadcast;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.domain.entity.DocumentOperation;
import ntg.documentation.example.domain.entity.User;
import ntg.documentation.example.repository.DocumentContentRepository;
import ntg.documentation.example.repository.DocumentOperationRepository;
import ntg.documentation.example.repository.DocumentRepository;
import ntg.documentation.example.service.CollaborationService;
import ntg.documentation.example.service.PermissionService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollaborationServiceImpl implements CollaborationService {

    private final DocumentOperationRepository operationRepo;
    private final DocumentContentRepository contentRepo;
    private final DocumentRepository documentRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final PermissionService permissionService;

    @Transactional
    @Override
    public void processOperation(UUID docId, UUID userId, EditOperationMessage msg) {

        if (!permissionService.canEdit(userId, docId)) {
            throw new RuntimeException("No edit permission");
        }

        if (operationRepo.existsByDocumentIdAndOpId(docId, msg.getOpId())) {
            return;
        }

        Document document = documentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Save operation (event log)
        DocumentOperation op = new DocumentOperation();
        op.setDocument(document);
        op.setUser(new User());
        op.getUser().setId(userId);

        op.setOperationType(mapType(msg.getType()));
        op.setPosition(msg.getPosition());
        op.setCharacter(msg.getCharacter());
        op.setOpId(msg.getOpId());
        op.setClientId(msg.getClientId());

        operationRepo.save(op);

        // apply operation to current content

        DocumentContent content = contentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        String updated = applyOperation(content.getContent(), msg);

        content.setContent(updated);
        content.setVersion(content.getVersion() + 1);

        contentRepo.save(content);

        // broadcast to all subscribers
        OperationBroadcast broadcast = new OperationBroadcast(
                docId,
                msg.getOpId(),
                msg.getClientId(),
                msg.getType(),
                msg.getPosition(),
                msg.getCharacter(),
                userId
        );

        messagingTemplate.convertAndSend(
                "/topic/doc/" + docId,
                broadcast
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

    private DocumentOperation.OperationType mapType(DocumentOperation.OperationType type) {
        return type == DocumentOperation.OperationType.INSERT
                ? DocumentOperation.OperationType.INSERT
                : DocumentOperation.OperationType.DELETE;
    }
}







