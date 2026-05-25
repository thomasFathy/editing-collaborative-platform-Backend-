package ntg.documentation.example.service;

import jakarta.transaction.Transactional;
import ntg.documentation.example.domain.dto.CursorMessage;
import ntg.documentation.example.domain.dto.DocumentSyncMessage;
import ntg.documentation.example.domain.dto.EditOperationMessage;
import ntg.documentation.example.domain.entity.DocumentContent;
import org.springframework.messaging.handler.annotation.DestinationVariable;

import java.util.Map;
import java.util.UUID;

public interface CollaborationService {

    @Transactional
    void processOperation(UUID docId, UUID userId, EditOperationMessage msg);

    void handleCursor(UUID docId, CursorMessage msg);

    DocumentContent getDocumentContent(UUID docId);

    void broadcastToDoc(UUID docId, Object payLoad);

    public void syncDocument(
            @DestinationVariable UUID docId,
            DocumentSyncMessage message);

}

