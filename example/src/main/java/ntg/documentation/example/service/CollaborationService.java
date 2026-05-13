package ntg.documentation.example.service;

import jakarta.transaction.Transactional;
import ntg.documentation.example.domain.dto.EditOperationMessage;

import java.util.UUID;

public interface CollaborationService {

    @Transactional
    void processOperation(UUID docId, UUID userId, EditOperationMessage msg);
}

