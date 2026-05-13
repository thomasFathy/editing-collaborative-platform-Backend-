package ntg.documentation.example.service;

import java.util.UUID;

public interface PermissionService {

    boolean canView(UUID userId, UUID docId);
    boolean canEdit(UUID userId, UUID docId);
}
