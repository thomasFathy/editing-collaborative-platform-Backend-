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
                .orElseThrow();

        Document doc = new Document();
        doc.setTitle(title);
        doc.setOwner(owner);

        documentRepository.save(doc);

        DocumentContent content =new DocumentContent();
        content.setDocument(doc);
        content.setContent("");
        content.setVersion(0L);

        documentContentRepository.save(content);

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
        return documentRepository.findByOwnerId(userId);
    }


    @Override
    public DocumentResponse getDocument(UUID docId, UUID userId) {

        if (!permissionService.canView(userId, docId)) {
            throw new RuntimeException("No access");
        }

        Document doc = documentRepository.findById(docId)
                .orElseThrow();

        DocumentContent content = documentContentRepository.findById(docId)
                .orElseThrow();

        return new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                content.getContent(),
                content.getVersion()
        );
    }
}
