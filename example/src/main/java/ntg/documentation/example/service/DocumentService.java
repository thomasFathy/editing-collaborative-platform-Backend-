package ntg.documentation.example.service;

import jakarta.transaction.Transactional;
import ntg.documentation.example.domain.dto.DocumentResponse;
import ntg.documentation.example.domain.dto.DocumentSyncMessage;
import ntg.documentation.example.domain.entity.Document;
import org.springframework.messaging.handler.annotation.DestinationVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse createDocument(String title, UUID userId);

    Document findById(UUID id);

    @Transactional
    DocumentResponse getDocument(UUID id);

    List<Document> getUserDocuments(UUID userId);


//    @Transactional
//    DocumentResponse getDocument(UUID docId);
}
