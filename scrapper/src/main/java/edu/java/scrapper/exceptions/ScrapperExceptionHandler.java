package edu.java.scrapper.exceptions;

import edu.java.model.dto.ApiErrorResponse;
import edu.java.model.exceptions.MalformedUrlException;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Log4j2
public class ScrapperExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INCORRECT_ARGUMENTS = "Request arguments are not to be omitted";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NotNull MethodArgumentNotValidException ex,
        @NotNull HttpHeaders headers,
        @NotNull HttpStatusCode status,
        @NotNull WebRequest request
    ) {
        return new ResponseEntity<>(new ApiErrorResponse(
            INCORRECT_ARGUMENTS, status.toString(), ex.getObjectName(), ex.getLocalizedMessage(),
            ex.getBindingResult().getFieldErrors().stream().map(FieldError::toString).toList()
        ), status);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    private ResponseEntity<ApiErrorResponse> handleUserAlreadyRegisteredException(Exception ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
            ex.getMessage(), HttpStatus.CONFLICT.toString(), ex.getClass().getName(), ex.getLocalizedMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UserNotRegisteredException.class, LinkNotTrackedException.class})
    private ResponseEntity<ApiErrorResponse> handleUserNotRegisteredException(Exception ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
            ex.getMessage(), HttpStatus.NOT_FOUND.toString(), ex.getClass().getName(), ex.getLocalizedMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MalformedUrlException.class, MissingRequestHeaderException.class,
        MethodArgumentTypeMismatchException.class})
    private ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(Exception ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
            ex.getMessage(), HttpStatus.BAD_REQUEST.toString(), ex.getClass().getName(), ex.getLocalizedMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        ), HttpStatus.BAD_REQUEST);
    }
}
