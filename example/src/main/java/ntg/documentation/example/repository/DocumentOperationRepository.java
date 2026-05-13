package ntg.documentation.example.repository;


import ntg.documentation.example.domain.entity.DocumentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentOperationRepository extends JpaRepository<DocumentOperation, UUID> {

    boolean existsByDocumentIdAndOpId(UUID documentId, String opId);

    List<DocumentOperation> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);
}