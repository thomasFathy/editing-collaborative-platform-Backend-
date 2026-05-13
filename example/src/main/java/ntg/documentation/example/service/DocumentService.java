package ntg.documentation.example.service;

import ntg.documentation.example.domain.dto.DocumentResponse;
import ntg.documentation.example.domain.entity.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse createDocument(String title, UUID userId);

    Document findById(UUID id);

    DocumentResponse getDocument(UUID id, UUID userId);

    List<Document> getUserDocuments(UUID userId);
}
