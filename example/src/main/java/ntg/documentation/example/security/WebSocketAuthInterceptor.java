package ntg.documentation.example.security;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.service.impl.JwtService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public WebSocketAuthInterceptor(@Lazy JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Catch the initial CONNECT frame
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String userIdStr = String.valueOf(jwtService.extractUserId(token));
                    UUID userId = UUID.fromString(userIdStr);
                    String email = jwtService.extractUsername(token);
                    if (accessor.getSessionAttributes() != null) {
                        accessor.getSessionAttributes().put("userId", userId);
                    }

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            email != null ? email : userIdStr,
                            null,
                            Collections.emptyList()
                    );

                    accessor.setUser(auth);

                } catch (Exception e) {
                    System.err.println("WebSocket JWT Validation Failed: " + e.getMessage());
                }
            }
        }
        return message;
    }
}