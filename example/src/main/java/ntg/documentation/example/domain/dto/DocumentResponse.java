package ntg.documentation.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DocumentResponse {
    private UUID id;
    private String title;
    private String content;
    private Long version;
}