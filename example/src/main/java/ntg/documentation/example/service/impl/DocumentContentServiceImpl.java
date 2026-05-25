package ntg.documentation.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.repository.DocumentContentRepository;
import ntg.documentation.example.service.DocumentContentService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentContentServiceImpl implements DocumentContentService {

    private final DocumentContentRepository contentRepository;

    @Transactional
    @Override
    public void saveDocumentContentManual(UUID docId, String updatedTextContent) {
        DocumentContent documentContent = contentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document content context missing"));

        documentContent.setContent(updatedTextContent);
        documentContent.setVersion(documentContent.getVersion() + 1);

        contentRepository.saveAndFlush(documentContent);
    }

    @Override
    public DocumentContent getContent(UUID documentId) {
        return contentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document content record not found"));
    }
}