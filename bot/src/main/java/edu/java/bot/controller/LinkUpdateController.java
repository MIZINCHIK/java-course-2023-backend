package edu.java.bot.controller;

import edu.java.bot.service.LinkUpdateService;
import edu.java.model.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkUpdateController implements LinkUpdateApi {
    private final LinkUpdateService service;

    @PostMapping(value = "/updates")
    @Override
    public ResponseEntity<?> sendUpdate(
        LinkUpdate update
    ) {
        service.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
