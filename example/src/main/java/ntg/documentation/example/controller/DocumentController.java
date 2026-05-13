package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.CreateDocumentRequest;
import ntg.documentation.example.domain.dto.DocumentResponse;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.service.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentResponse create(@RequestBody CreateDocumentRequest request,
                                   Principal principal) {

        UUID userId = UUID.fromString(principal.getName());

        return documentService.createDocument(request.getTitle(), userId);
    }

    @GetMapping
    public List<Document> getMyDocs(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        return documentService.getUserDocuments(userId);
    }

    @GetMapping("/{id}")
    public DocumentResponse get(@PathVariable UUID id,
                                Principal principal) {

        UUID userId = UUID.fromString(principal.getName());

        return documentService.getDocument(id, userId);
    }
}