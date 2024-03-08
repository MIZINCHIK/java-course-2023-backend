package edu.java.scrapper.controller;

import edu.java.model.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

public interface TgChatApi {
    @Operation(summary = "Проверить, отслеживается ли чат",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Чат успешно получен"),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "404", description = "Чат не отслеживается")})
    ResponseEntity<Void> getChat(
        @NotNull
        Long id
    );

    @Operation(summary = "Зарегистрировать чат",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "409", description = "Чат уже зарегистрирован", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   })})
    ResponseEntity<Void> registerChat(
        @NotNull
        Long id
    );

    @Operation(summary = "Удалить чат",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "404", description = "Чат не существует", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   })
               })
    ResponseEntity<Void> deleteChat(
        @NotNull
        Long id
    );
}
