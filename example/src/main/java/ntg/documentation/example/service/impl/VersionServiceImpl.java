package ntg.documentation.example.service.impl;

import jakarta.transaction.Transactional;
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
                .orElseThrow(()->new RuntimeException("no content found in this doc's id "+docId));

        Document doc = documentRepo.findById(docId)
                .orElseThrow(()->new RuntimeException("No document found with this id "+docId));

        DocumentVersion version = new DocumentVersion();
        version.setDocument(doc);
        version.setContent(content.getContent());
        version.setVersion(content.getVersion());
        versionRepo.save(version);
    }

    @Override
    @Transactional
    public void restoreVersion(UUID versionId, UUID userId) {

        DocumentVersion version = versionRepo.findById(versionId)
                .orElseThrow(()->new RuntimeException("no version found with this id: "+ versionId));

        UUID docId = version.getDocument().getId();

        if (!permissionService.canEdit(userId, docId)) {
            throw new RuntimeException("No permission");
        }

        DocumentContent content = contentRepo.findById(docId)
                .orElseThrow();

        content.setContent(version.getContent());
        content.setVersion(content.getVersion() + 1);
        contentRepo.save(content);

        messagingTemplate.convertAndSend(
                "/topic/doc/" + docId,
                new FullContentUpdate(content.getContent())
        );


    }


    @Transactional
    public List<VersionResponse> getVersions(UUID docId, UUID userId) {

        if (!permissionService.canView(userId, docId)) {
            throw new RuntimeException("No access");
        }

        return versionRepo.findByDocumentIdOrderByCreatedAtDesc(docId).stream().
                map(version-> VersionResponse
                        .builder()
                        .content(version.getContent())
                        .createdAt(version.getCreatedAt())
                        .updatedAt(version.getUpdatedAt())
                        .ownerEmail(version.getDocument().getOwner().getEmail())
                        .version(version.getVersion())


                        .build())
                .toList();
    }



}

