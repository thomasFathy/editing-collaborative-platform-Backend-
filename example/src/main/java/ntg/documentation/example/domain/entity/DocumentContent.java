package ntg.documentation.example.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "document_contents")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

public class DocumentContent {

    @Id
    private UUID documentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(nullable = false,columnDefinition = "Text")
    private String content;

    @Column(nullable = false)
    private Long version = 0L;
}