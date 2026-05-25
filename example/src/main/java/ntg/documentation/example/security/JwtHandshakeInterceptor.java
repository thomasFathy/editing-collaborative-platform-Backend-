package ntg.documentation.example.security;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ntg.documentation.example.service.impl.JwtService;
import java.util.Map;

@Component

public class JwtHandshakeInterceptor implements HandshakeInterceptor {// this interface for the negotiation phase before switching the protocol from http->WebSocket

    @Autowired
    private JwtService jwtService;

    @Override
        public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = extractToken(request);

        if (token != null && jwtService.isValid(token)) {
            attributes.put("userId", jwtService.extractUserId(token));
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);

        }
        return null;

    }
}
