package ntg.documentation.example.service.impl;

import lombok.RequiredArgsConstructor;
import ntg.documentation.example.domain.dto.DocumentResponse;
import ntg.documentation.example.domain.dto.ShareRequest;
import ntg.documentation.example.domain.entity.Document;
import ntg.documentation.example.domain.entity.DocumentShare;
import ntg.documentation.example.domain.entity.User;
import ntg.documentation.example.repository.DocumentRepository;
import ntg.documentation.example.repository.DocumentShareRepository;
import ntg.documentation.example.repository.UserRepository;
import ntg.documentation.example.service.ShareService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentShareRepository shareRepository;

    @Override
    public void share(UUID docId, UUID ownerId, ShareRequest req){

        Document doc = documentRepository.findById(docId).orElseThrow();

        if(!doc.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Only owner can share");
        }

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with email: " + req.getEmail()));

        DocumentShare share = new DocumentShare();


        share.setDocument(doc);
        share.setUser(user);
        share.setRole(req.getRole());

        shareRepository.save(share);
    }
}
