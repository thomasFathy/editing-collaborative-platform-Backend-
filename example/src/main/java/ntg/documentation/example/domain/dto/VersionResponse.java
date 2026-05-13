package ntg.documentation.example.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class VersionResponse {

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ownerEmail;
    private Long version;



}
