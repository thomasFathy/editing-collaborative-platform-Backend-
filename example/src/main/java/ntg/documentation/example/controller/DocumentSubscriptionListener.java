package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.repository.DocumentContentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class DocumentSubscriptionListener {

    private final DocumentContentRepository contentRepo;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Pattern DOCUMENT_TOPIC_PATTERN =
            Pattern.compile("^/topic/doc/([a-fA-F0-9\\-]+)$");

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination == null) return;

        Matcher matcher = DOCUMENT_TOPIC_PATTERN.matcher(destination);
        if (matcher.matches()) {
            try {
                UUID docId = UUID.fromString(matcher.group(1));
                String sessionId = accessor.getSessionId();
                DocumentContent documentContent = contentRepo.findById(docId).orElse(null);
                String currentText = (documentContent != null) ? documentContent.getContent() : "";
                Map<String, Object> syncPayload = new HashMap<>();
                syncPayload.put("content", currentText);

                messagingTemplate.convertAndSendToUser(
                        sessionId,
                        "/queue/sync",
                        syncPayload,
                        createHeaders(sessionId)
                );

            } catch (IllegalArgumentException e) {
            }
        }
    }

    private Map<String, Object> createHeaders(String sessionId) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
