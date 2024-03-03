package edu.java.scrapper.controller;

import edu.java.model.dto.AddLinkRequest;
import edu.java.model.dto.ApiErrorResponse;
import edu.java.model.dto.LinkResponse;
import edu.java.model.dto.ListLinksResponse;
import edu.java.model.dto.RemoveLinkRequest;
import edu.java.model.exceptions.MalformedUrlException;
import edu.java.model.links.Link;
import edu.java.model.storage.LinkStorage;
import edu.java.model.storage.UserStorage;
import edu.java.scrapper.exceptions.LinkNotTrackedException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/links")
public class LinksController {
    private final UserStorage userStorage;
    private final LinkStorage linkStorage;

    @GetMapping(produces = {"application/json"})
    @Operation(summary = "Получить все отслеживаемые ссылки",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Ссылки успешно получены", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ListLinksResponse.class))
                   }),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "404", description = "Юзер не зарегистрирован", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   })})
    private ResponseEntity<ListLinksResponse> getLinks(
        @NotNull
        @Parameter(name = "Tg-Chat-Id", required = true, in = ParameterIn.HEADER)
        @RequestHeader(value = "Tg-Chat-Id")
        Long tgChatId
    ) {
        if (!userStorage.isUserRegistered(tgChatId)) {
            throw new UserNotRegisteredException();
        }
        List<LinkResponse> links = linkStorage.getLinksByUserId(tgChatId);
        return new ResponseEntity<>(new ListLinksResponse(links, links.size()), HttpStatus.OK);
    }

    @PostMapping(produces = {"application/json"},
                 consumes = {"application/json"})
    @Operation(summary = "Добавить отслеживание ссылки",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена", content = {
                       @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
                   }),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "404", description = "Юзер не зарегистрирован", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   })})
    private ResponseEntity<LinkResponse> postLink(
        @NotNull
        @Parameter(name = "Tg-Chat-Id", required = true, in = ParameterIn.HEADER)
        @RequestHeader(value = "Tg-Chat-Id")
        Long tgChatId,
        @Parameter(name = "AddLinkRequest", required = true)
        @Valid
        @RequestBody
        AddLinkRequest addLinkRequest
    ) {
        if (!userStorage.isUserRegistered(tgChatId)) {
            throw new UserNotRegisteredException();
        }
        Link link;
        try {
            link = new Link(addLinkRequest.link().toString());
        } catch (Exception e) {
            throw new MalformedUrlException();
        }
        Long id = linkStorage.trackLink(link, tgChatId);
        return ResponseEntity.ok(new LinkResponse(id, addLinkRequest.link()));
    }

    @DeleteMapping(produces = {"application/json"})
    @Operation(summary = "Убрать отслеживание ссылки",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана", content = {
                       @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
                   }),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "404",
                                description = "Ссылка не найдена или юзер не зарегистрирован",
                                content = {
                                    @Content(mediaType = "application/json",
                                             schema = @Schema(implementation = ApiErrorResponse.class))
                                })})
    private ResponseEntity<LinkResponse> deleteLink(
        @NotNull
        @Parameter(name = "Tg-Chat-Id", required = true, in = ParameterIn.HEADER)
        @RequestHeader(value = "Tg-Chat-Id")
        Long tgChatId,
        @Parameter(name = "RemoveLinkRequest", required = true)
        @Valid
        @RequestBody
        RemoveLinkRequest removeLinkRequest
    ) {
        if (!userStorage.isUserRegistered(tgChatId)) {
            throw new UserNotRegisteredException();
        }
        Link link;
        try {
            link = new Link(removeLinkRequest.link().toString());
        } catch (Exception e) {
            throw new MalformedUrlException();
        }
        if (!linkStorage.isLinkTracked(link, tgChatId)) {
            throw new LinkNotTrackedException();
        }
        Long id = linkStorage.untrackLink(link, tgChatId);
        return ResponseEntity.ok(new LinkResponse(id, removeLinkRequest.link()));
    }
}
