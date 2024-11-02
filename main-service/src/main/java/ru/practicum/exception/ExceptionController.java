package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.common.constants.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String reason = e.getBody().getDetail();
        String field;
        if (!Objects.requireNonNull(e.getBindingResult().getFieldError()).getField().isEmpty()) {
            field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        } else {
            field = e.getMessage();
        }
        String message = "Field: " + field +
                " error: " + e.getBindingResult().getFieldError().getDefaultMessage();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final ConflictException e) {
        String reason = "Integrity constraint has been violated";
        String message = e.getMessage();
        return new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final DataIntegrityViolationException e) {
        String reason = "Integrity constraint has been violated";
        String message = "could not execute statement; constraint " + e.getMostSpecificCause();
        return new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        String reason = "The required object was not found.";
        String message = e.getMessage();
        return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        String reason = "Something went wrong";
        String message = e.getMessage();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                reason, message, prepareResponseDate());
    }

    private String prepareResponseDate() {
        return LocalDateTime.now().format(Constants.DATE_FORMATTER);
    }
}
