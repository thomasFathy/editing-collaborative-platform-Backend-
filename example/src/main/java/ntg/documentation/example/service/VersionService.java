package ntg.documentation.example.service;

import ntg.documentation.example.domain.dto.VersionResponse;
import ntg.documentation.example.domain.entity.DocumentVersion;

import java.util.List;
import java.util.UUID;

public interface VersionService {

    void createSnapshot(UUID docId, UUID userId);
    void restoreVersion(UUID versionId, UUID userId);
//    List<DocumentVersion> getVersions(UUID docId, UUID userId);
    List <VersionResponse> getVersions(UUID docId, UUID userId);
}
