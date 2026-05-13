package ntg.documentation.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ntg.documentation.example.domain.entity.DocumentOperation;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OperationBroadcast {

    private UUID documentId;

    private String opId;
    private String clientId;

    private DocumentOperation.OperationType type;

    private Integer position;
    private String character;

    private UUID userId;
}