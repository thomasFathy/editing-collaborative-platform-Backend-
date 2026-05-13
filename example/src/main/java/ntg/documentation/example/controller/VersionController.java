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
@RequestMapping("/version")
public class VersionController {

    private final VersionService versioningService;

    @PostMapping("/{docId}/versions")
    public void createSnapshot(@PathVariable UUID docId,
                               Principal principal){

        UUID userId = UUID.fromString(principal.getName());
        versioningService.createSnapshot(docId, userId);
    }

    @GetMapping("/{docId}/versions")
    public List<VersionResponse> getVersions(@PathVariable UUID docId,
                                             Principal principal) {

        UUID userId = UUID.fromString(principal.getName());
        return versioningService.getVersions(docId, userId);
    }


    @PostMapping("/versions/{versionId}/restore")
        public void restoreVersions(@PathVariable UUID docId,
                                    Principal principal) {

        UUID userId = UUID.fromString(principal.getName());
         versioningService.restoreVersion(docId, userId);


    }

}
