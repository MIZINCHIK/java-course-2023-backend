package edu.java.scrapper.controller;

import edu.java.model.dto.ApiErrorResponse;
import edu.java.model.storage.UserStorage;
import edu.java.scrapper.exceptions.UserAlreadyRegisteredException;
import edu.java.scrapper.exceptions.UserNotRegisteredException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tg-chat")
public class TgChatController {
    private final UserStorage userStorage;

    @GetMapping(value = "/{id}",
                produces = {"application/json"})
    @Operation(summary = "Проверить, отслеживается ли чат",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Чат успешно получен"),
                   @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ApiErrorResponse.class))
                   }),
                   @ApiResponse(responseCode = "404", description = "Чат не отслеживается")})
    private ResponseEntity<Void> getChat(
        @Parameter(name = "id",
                   required = true,
                   in = ParameterIn.PATH)
        @PathVariable("id")
        @NotNull
        Long id
    ) {
        if (userStorage.isUserRegistered(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/{id}",
                 produces = {"application/json"})
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
    private ResponseEntity<Void> registerChat(
        @Parameter(name = "id",
                   required = true,
                   in = ParameterIn.PATH)
        @PathVariable("id")
        @NotNull
        Long id
    ) {
        if (userStorage.isUserRegistered(id)) {
            throw new UserAlreadyRegisteredException();
        }
        userStorage.registerUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}",
                   produces = {"application/json"})
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
    private ResponseEntity<Void> deleteChat(
        @Parameter(name = "id",
                   required = true,
                   in = ParameterIn.PATH)
        @PathVariable("id")
        @NotNull
        Long id
    ) {
        if (!userStorage.isUserRegistered(id)) {
            throw new UserNotRegisteredException();
        }
        userStorage.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
