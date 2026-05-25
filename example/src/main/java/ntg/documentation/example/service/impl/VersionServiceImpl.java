package ntg.documentation.example.service.impl;

//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.FullContentUpdate;
import ntg.documentation.example.domain.dto.VersionResponse;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.domain.entity.DocumentVersion;
import ntg.documentation.example.repository.DocumentContentRepository;
import ntg.documentation.example.repository.DocumentRepository;
import ntg.documentation.example.repository.DocumentVersionRepository;
import ntg.documentation.example.service.PermissionService;
import ntg.documentation.example.service.VersionService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService {

    private final PermissionService permissionService;
    private final DocumentContentRepository contentRepo;
    private final DocumentRepository documentRepo;
    private final DocumentVersionRepository versionRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void createSnapshot(UUID docId, UUID userId) {
        if (!permissionService.canView(userId, docId)) {
            throw new RuntimeException("No access");
        }

        DocumentContent content = contentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("No content found in this doc's id " + docId));

        Document doc = documentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("No document found with this id " + docId));

        // 🔥 FIX 1: Increment nextVersion BEFORE assigning it to the historical record.
        // Otherwise, your active document version and snapshot historical versions decouple!
        long nextVersion = content.getVersion() + 1;

        DocumentVersion version = new DocumentVersion();
        version.setDocument(doc);
        version.setContent(content.getContent());
        version.setVersion(nextVersion); // Assign the incremented index here

        content.setVersion(nextVersion);

        contentRepo.save(content);
        versionRepo.save(version);
    }

    @Override
    @Transactional
    public void restoreVersion(UUID versionId, UUID userId) {
        DocumentVersion version = versionRepo.findById(versionId)
                .orElseThrow(() -> new RuntimeException("No version found with this id: " + versionId));

        UUID docId = version.getDocument().getId();
        System.out.println("there is the docId"+docId);

        if (!permissionService.canEdit(userId, docId)) {
            throw new RuntimeException("No permission");
        }

        DocumentContent content = contentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("No live content found to restore into"));

        content.setContent(version.getContent());
        content.setVersion(content.getVersion() + 1);
        contentRepo.save(content);

        // This is excellent! Broadcasting the payload alerts all active frontend viewports instantly.
        messagingTemplate.convertAndSend(
                "/topic/doc/" + docId,
                new FullContentUpdate(content.getContent())
        );
    }

    @Override
    @Transactional(readOnly = true) // Changed to readOnly for clean query performance
    public List<VersionResponse> getVersions(UUID docId, UUID userId) {
        if (!permissionService.canView(userId, docId)) {
            throw new RuntimeException("No access");
        }

        return versionRepo.findByDocumentIdOrderByVersionDesc(docId).stream()
                .map(version -> VersionResponse.builder()
                        .id(version.getId()) // 🔥 FIXED: This passes the critical UUID token down to your frontend!
                        .content(version.getContent())
                        .createdAt(version.getCreatedAt())
                        .updatedAt(version.getUpdatedAt())
                        .ownerEmail(version.getDocument().getOwner() != null ? version.getDocument().getOwner().getEmail() : "System")
                        .version(version.getVersion())
                        .build())
                .toList();
    }
}