package ntg.documentation.example.service;

import ntg.documentation.example.domain.dto.ShareRequest;

import java.util.UUID;

public interface ShareService {

  void share(UUID docId, UUID ownerId, ShareRequest req);

}

