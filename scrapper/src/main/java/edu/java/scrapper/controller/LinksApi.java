package edu.java.scrapper.controller;

import edu.java.model.dto.AddLinkRequest;
import edu.java.model.dto.ApiErrorResponse;
import edu.java.model.dto.LinkResponse;
import edu.java.model.dto.ListLinksResponse;
import edu.java.model.dto.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface LinksApi {
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
    ResponseEntity<ListLinksResponse> getLinks(
        @NotNull
        Long tgChatId
    );

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
    ResponseEntity<LinkResponse> postLink(
        @NotNull
        @RequestHeader(value = "Tg-Chat-Id")
        Long tgChatId,
        @RequestBody
        AddLinkRequest addLinkRequest
    );

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
    ResponseEntity<LinkResponse> deleteLink(
        @NotNull
        @RequestHeader(value = "Tg-Chat-Id")
        Long tgChatId,
        @RequestBody
        RemoveLinkRequest removeLinkRequest
    );
}
