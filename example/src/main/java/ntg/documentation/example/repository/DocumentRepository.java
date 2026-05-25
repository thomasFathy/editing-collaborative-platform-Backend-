package ntg.documentation.example.repository;

import ntg.documentation.example.domain.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByOwnerId(UUID ownerId);
//    Document findByTitle(String title);

    @Query("SELECT DISTINCT d FROM Document d " +
            "LEFT JOIN DocumentShare s ON s.document.id = d.id " +
            "WHERE d.owner.id = :userId OR s.user.id = :userId")
    List<Document> getUserDocuments(@Param("userId") UUID userId);


    boolean existsByTitle(String title);
}