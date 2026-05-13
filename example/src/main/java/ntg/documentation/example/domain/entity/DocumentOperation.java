package ntg.documentation.example.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "document_operations",
        indexes = {
                @Index(name = "idx_doc_ops_doc", columnList = "document_id"),
                @Index(name = "idx_doc_ops_created", columnList = "createdAt")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "opId"})
)
@Getter
@Setter

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentOperation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operationType;

    private Integer position;

    private String character;

    @Column(nullable = false)
    private String opId;

    private String clientId;

    public enum OperationType {
        INSERT,
        DELETE
    }
}