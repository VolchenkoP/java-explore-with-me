package ru.practicum.exception;

public class ClientException extends RuntimeException {

    public ClientException(final String message) {
        super(message);
    }
}
