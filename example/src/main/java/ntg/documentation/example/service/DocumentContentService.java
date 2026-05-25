package ntg.documentation.example.service;

import ntg.documentation.example.domain.entity.DocumentContent;

import java.util.UUID;

public interface DocumentContentService {

    void saveDocumentContentManual(UUID docId, String content);
    DocumentContent getContent(UUID documentId);
}
