package ntg.documentation.example.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "document_shares",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"}))
@Getter
@Setter

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentShare extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        VIEWER,
        EDITOR
    }
}