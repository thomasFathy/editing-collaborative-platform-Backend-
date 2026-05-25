package ntg.documentation.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.DocumentResponse;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.domain.entity.User;
import ntg.documentation.example.repository.DocumentContentRepository;
import ntg.documentation.example.repository.DocumentRepository;
import ntg.documentation.example.repository.UserRepository;
import ntg.documentation.example.service.DocumentService;
import ntg.documentation.example.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final DocumentContentRepository documentContentRepository;
    private final PermissionService permissionService;

    @Transactional
    @Override
    public DocumentResponse createDocument(String title, UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (documentRepository.existsByTitle(title)) {
            throw new RuntimeException("Document already exists with this title");
        }

        Document doc = new Document();
        doc.setTitle(title);
        doc.setOwner(owner);

        doc = documentRepository.saveAndFlush(doc);

        DocumentContent content = new DocumentContent();
        content.setDocument(doc);
        content.setContent("");
        content.setVersion(0L);

        content = documentContentRepository.saveAndFlush(content);

        return new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                content.getContent(),
                content.getVersion()
        );
    }

    @Override
    public Document findById(UUID id) {
        return documentRepository.findById(id).orElseThrow();
    }

    public List<Document> getUserDocuments(UUID userId) {
        return documentRepository.getUserDocuments(userId);
    }

    @Transactional
    @Override
    public DocumentResponse getDocument(UUID docId) {
        // 1. Find the document metadata
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // 2. 🔥 FIX: Use built-in findById() and wrap it in .orElse(null) so your self-healing block works!
        DocumentContent content = documentContentRepository.findById(docId).orElse(null);

        // 3. Self-healing null check block
        if (content == null) {
            DocumentContent newContent = new DocumentContent();
            newContent.setDocument(doc);
            newContent.setContent("");
            newContent.setVersion(0L);

            // Save and flush immediately to eliminate database racing conditions
            content = documentContentRepository.saveAndFlush(newContent);
        }

        return new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                content.getContent(),
                content.getVersion()
        );
    }
}