package ntg.documentation.example.domain.dto;

import lombok.Getter;
import lombok.Setter;
import ntg.documentation.example.domain.entity.DocumentShare;

import java.util.UUID;

@Getter
@Setter
public class ShareRequest {
    private String email;
    private DocumentShare.Role role;
}
