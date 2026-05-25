package ntg.documentation.example.controller;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.DocumentResponse;
import ntg.documentation.example.domain.dto.SaveContentRequest;
import ntg.documentation.example.domain.entity.DocumentContent;
import ntg.documentation.example.service.DocumentContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/document-contents")
@RequiredArgsConstructor
public class DocumentContentController {

    private final DocumentContentService service;

    @PutMapping("/{documentId}")
    public ResponseEntity<Void> saveContent(
            @PathVariable UUID documentId,
            @RequestBody SaveContentRequest request
    ) {

        service.saveDocumentContentManual(documentId, request.getContent());

        return ResponseEntity.ok().build();
    }


    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getContent(@PathVariable UUID documentId) {
        DocumentContent content = service.getContent(documentId);

        return ResponseEntity.ok(new DocumentResponse(
                content.getDocumentId(),
                content.getDocument().getTitle(),
                content.getContent(),
                content.getVersion()
        ));


    }






}