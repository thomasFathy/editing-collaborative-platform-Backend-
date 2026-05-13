package ntg.documentation.example.repository;

import ntg.documentation.example.domain.entity.DocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentShareRepository extends JpaRepository<DocumentShare, UUID> {

    Optional<DocumentShare> findByDocumentIdAndUserId(UUID documentId, UUID userId);

    boolean existsByDocumentIdAndUserId(UUID documentId, UUID userId);
}