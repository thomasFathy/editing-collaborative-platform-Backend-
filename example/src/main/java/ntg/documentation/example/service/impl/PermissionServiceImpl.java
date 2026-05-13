package ntg.documentation.example.service.impl;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.domain.entity.DocumentShare;
import ntg.documentation.example.repository.DocumentRepository;
import ntg.documentation.example.repository.DocumentShareRepository;
import ntg.documentation.example.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final DocumentRepository documentRepository;
    private final DocumentShareRepository shareRepository;


    @Override
    public boolean canView(UUID userId, UUID docId) {
        Document doc = documentRepository.findById(docId).orElseThrow(()-> new RuntimeException("no document found"));
        if(doc.getOwner().getId().equals(userId)){
            return true;
        }

        return shareRepository.existsByDocumentIdAndUserId(docId, userId);
    }

    @Override
    public boolean canEdit(UUID userId, UUID docId) {
        Document doc = documentRepository.findById(docId).orElseThrow(()-> new RuntimeException("no document found"));
        if (doc.getOwner().getId().equals(userId)) return true;

        return shareRepository.findByDocumentIdAndUserId(docId, userId)
                .map(share -> share.getRole() == DocumentShare.Role.EDITOR)
                .orElse(false);


    }
}
