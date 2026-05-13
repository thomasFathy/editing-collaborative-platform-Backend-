package ntg.documentation.example.domain.dto;

import lombok.Getter;
import lombok.Setter;
import ntg.documentation.example.domain.entity.DocumentOperation;

import java.util.UUID;

@Getter
@Setter
public class EditOperationMessage {

    private UUID documentId;

    private String opId;
    private String clientId;

    private DocumentOperation.OperationType type;
    private Integer position;
    private String character;
}