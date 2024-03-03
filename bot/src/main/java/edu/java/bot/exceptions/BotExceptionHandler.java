package edu.java.bot.exceptions;

import edu.java.model.dto.ApiErrorResponse;
import edu.java.model.exceptions.MalformedUrlException;
import java.net.URI;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BotExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String URI = "URI provided is either malformed (include the full one) or not yet tracked";
    private static final String OOPS = "Oops";
    private static final String DEFAULT = "Malformed request: arguments mustn't be omitted";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NotNull MethodArgumentNotValidException ex,
        @NotNull HttpHeaders headers,
        @NotNull HttpStatusCode status,
        @NotNull WebRequest request
    ) {
        String description;
        try {
            description = switch (ex.getBindingResult().getFieldErrors().getFirst().getRejectedValue()) {
                case URI ignored -> URI;
                case null, default -> DEFAULT;
            };
        } catch (Exception e) {
            description = OOPS;
        }
        return new ResponseEntity<>(new ApiErrorResponse(
            description, status.toString(), ex.getObjectName(), ex.getLocalizedMessage(),
            ex.getBindingResult().getFieldErrors().stream().map(FieldError::toString).toList()
        ), status);
    }

    @ExceptionHandler(MalformedUrlException.class)
    private ResponseEntity<ApiErrorResponse> handleMalformedUrlException(Exception ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
            ex.getMessage(), HttpStatus.BAD_REQUEST.toString(), ex.getClass().getName(), ex.getLocalizedMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        ), HttpStatus.BAD_REQUEST);
    }
}
