package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.CursorMessage;
import ntg.documentation.example.domain.dto.EditOperationMessage;
import ntg.documentation.example.service.CollaborationService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ntg.documentation.example.domain.dto.DocumentSyncMessage;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor

public class CollaborationController {

    private final CollaborationService collaborationService;


    @MessageMapping("/edit/{docId}")
    public void handleEdit(@DestinationVariable UUID docId, EditOperationMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (msg == null || msg.getOpId() == null) {
                System.err.println("WS Incoming payload dropped: Invalid or empty operational framework signature identity.");
                return;
            }

            UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
            collaborationService.processOperation(docId, userId, msg);

        } catch (Exception e) {
            System.err.println("Error encountered during transactional workspace processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @MessageMapping("/cursor/{docId}")
    public void handleCursor(
            @DestinationVariable UUID docId,
            CursorMessage msg,
            Principal principal
    ) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized connection attempt");
        }

        if (msg.getEmail() == null || msg.getEmail().isBlank()) {
            msg.setEmail("collaborator@system.com");
        }

        collaborationService.handleCursor(docId,msg);
    }

    @MessageMapping("/load/{docId}")
    public void loadInitialContent(
            @DestinationVariable UUID docId,
            @Payload java.util.Map<String, String> payload, // Receives the unique clientId payload wrapper
            Principal principal
    ) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized document catch-up request.");
        }

        String clientId = payload.get("clientId");

        ntg.documentation.example.domain.entity.DocumentContent contentEntity =
                collaborationService.getDocumentContent(docId); // Make sure this getter method is exposed in your service
        String currentText = (contentEntity != null) ? contentEntity.getContent() : "";

        java.util.Map<String, Object> syncResponse = new java.util.HashMap<>();
        syncResponse.put("clientId", "SYSTEM_SYNC"); // Prevents self-filtering drop loops
        syncResponse.put("content", currentText);

       collaborationService.broadcastToDoc(docId, syncResponse);
    }


    @MessageMapping("/collab/{docId}")
    public void syncDocument(
            @DestinationVariable UUID docId,
            DocumentSyncMessage message
    ) {


        collaborationService.syncDocument(docId, message);


    }

}