package edu.java.scrapper.controller;

import edu.java.model.dto.AddLinkRequest;
import edu.java.model.dto.LinkResponse;
import edu.java.model.dto.ListLinksResponse;
import edu.java.model.dto.RemoveLinkRequest;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.model.links.Link;
import edu.java.model.storage.UserStorage;
import edu.java.scrapper.exceptions.LinkNotTrackedException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import edu.java.scrapper.service.ModifiableLinkStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/links")
public class LinksController implements LinksApi {
    private final UserStorage userService;
    private final ModifiableLinkStorage linkService;

    @GetMapping
    @Override
    public ResponseEntity<ListLinksResponse> getLinks(
        Long tgChatId
    ) {
        if (!userService.isUserRegistered(tgChatId)) {
            throw new UserNotRegisteredException();
        }
        List<LinkResponse> links = linkService.getLinksByUserId(tgChatId);
        return new ResponseEntity<>(new ListLinksResponse(links, links.size()), HttpStatus.OK);
    }

    @PostMapping
    @Override
    public ResponseEntity<LinkResponse> postLink(
        Long tgChatId,
        AddLinkRequest addLinkRequest
    ) {
        if (!userService.isUserRegistered(tgChatId)) {
            throw new UserNotRegisteredException();
        }
        Link link;
        try {
            link = new Link(addLinkRequest.link().toString());
        } catch (Exception e) {
            throw new MalformedUrlException();
        }
        Long id = linkService.trackLink(link, tgChatId);
        return ResponseEntity.ok(new LinkResponse(id, addLinkRequest.link()));
    }

    @DeleteMapping
    @Override
    public ResponseEntity<LinkResponse> deleteLink(
        Long tgChatId,
        RemoveLinkRequest removeLinkRequest
    ) {
        if (!userService.isUserRegistered(tgChatId)) {
            throw new UserNotRegisteredException();
        }
        Link link;
        try {
            link = new Link(removeLinkRequest.link().toString());
        } catch (Exception e) {
            throw new MalformedUrlException();
        }
        if (!linkService.isLinkTracked(link, tgChatId)) {
            throw new LinkNotTrackedException();
        }
        Long id = linkService.untrackLink(link, tgChatId);
        return ResponseEntity.ok(new LinkResponse(id, removeLinkRequest.link()));
    }
}
