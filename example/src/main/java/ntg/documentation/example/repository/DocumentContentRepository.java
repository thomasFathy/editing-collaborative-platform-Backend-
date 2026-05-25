package ntg.documentation.example.repository;

import ntg.documentation.example.domain.entity.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContent, UUID> {
//    DocumentContent saveDocumentContentManual(UUID documentId);

}