package edu.java.scrapper.service;

import edu.java.model.storage.LinkStorage;
import edu.java.scrapper.dto.LinkDto;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public interface ModifiableLinkStorage extends LinkStorage {
    List<LinkDto> getLinksWithExpiredCheckTime(Duration expirationInterval);

    void updateLink(long linkId, OffsetDateTime time);

    List<Long> getUsersByLink(Long id);
}
