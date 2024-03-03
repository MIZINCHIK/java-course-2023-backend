package edu.java.bot.controller;

import edu.java.bot.service.LinkUpdateService;
import edu.java.model.dto.ApiErrorResponse;
import edu.java.model.dto.LinkUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkUpdateController {
    private final LinkUpdateService service;

    @PostMapping(value = "/updates",
                 produces = {"application/json"},
                 consumes = {"application/json"})
    @Operation(summary = "Отправить обновление")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Обновление обработано"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = {@Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ApiErrorResponse.class))})})
    private ResponseEntity<?> sendUpdate(
        @Parameter(name = "LinkUpdate", required = true) @Valid @RequestBody LinkUpdate update
    ) {
        service.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
