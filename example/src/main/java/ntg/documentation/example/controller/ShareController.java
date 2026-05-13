package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.ShareRequest;
import ntg.documentation.example.service.ShareService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService sharingService;

    @PostMapping("/{docId}/share")
    public void share(@PathVariable UUID docId,
                      @RequestBody ShareRequest request,
                      Principal principal) {

        UUID ownerId = UUID.fromString(principal.getName());

        sharingService.share(docId, ownerId, request);
    }
}