package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.VersionResponse;
import ntg.documentation.example.service.VersionService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/version") // Prefixed with /api to separate from static routing
public class VersionController {

    private final VersionService versioningService;

    @PostMapping("/{docId}/snapshot")
    public void createSnapshot(@PathVariable UUID docId, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        versioningService.createSnapshot(docId, userId);
    }

    @GetMapping("/{docId}")
    public List<VersionResponse> getVersions(@PathVariable UUID docId, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        return versioningService.getVersions(docId, userId);
    }

    @PostMapping("/restore/{versionId}")
    public void restoreVersions(@PathVariable UUID versionId, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        versioningService.restoreVersion(versionId, userId);
    }
    private UUID getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized user context");
        }
        return UUID.fromString(principal.getName());
    }
}