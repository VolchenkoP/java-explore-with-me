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
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String reason = e.getBody().getDetail();
        String field;
        if (!Objects.requireNonNull(e.getBindingResult().getFieldError()).getField().isEmpty()) {
            field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        } else {
            field = e.getMessage();
        }
        String message = "Field: " + field +
                " error: " + e.getBindingResult().getFieldError().getDefaultMessage();
        return new ErrorMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleConflict(ConflictException e) {
        String reason = "Integrity constraint has been violated";
        String message = e.getMessage();
        return new ErrorMessage(HttpStatus.CONFLICT.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleConflict(DataIntegrityViolationException e) {
        String reason = "Ограничение целостности нарушено";
        String message = "Не удалось выполнить, ограничение: " + e.getMostSpecificCause();
        return new ErrorMessage(HttpStatus.CONFLICT.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(NotFoundException e) {
        String reason = "Объект поиска не найден.";
        String message = e.getMessage();
        return new ErrorMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(ValidationException e) {
        String reason = "Неправильный запрос.";
        String message = e.getMessage();
        return new ErrorMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String reason = "Неправильный запрос.";
        String message = e.getMessage();
        return new ErrorMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseDate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleThrowable(Throwable e) {
        String reason = "Внутренняя ошибка сервера";
        String message = e.getMessage();
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                reason, message, prepareResponseDate());
    }

    private String prepareResponseDate() {
        return LocalDateTime.now().format(Constants.DATE_FORMATTER);
    }
}
