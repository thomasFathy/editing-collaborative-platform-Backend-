package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.EditOperationMessage;
import ntg.documentation.example.service.CollaborationService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CollaborationController {

    private final CollaborationService collaborationService;


    @MessageMapping("/edit/{docId}")
    public void handleEdit(@DestinationVariable UUID docId,
                           @Payload EditOperationMessage message,
                           Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        UUID userId = UUID.fromString(principal.getName());

        // validate payload consistency
        if (message.getDocumentId() == null ||
                !message.getDocumentId().equals(docId)) {
            throw new RuntimeException("Document mismatch");
        }

        if (message.getOpId() == null || message.getOpId().isBlank()) {
            throw new RuntimeException("Invalid operation id");
        }

        if (message.getType() == null) {
            throw new RuntimeException("Operation type required");
        }

        collaborationService.processOperation(docId, userId, message);
    }
}