package ru.discomfortDeliverer.exceptions;

import java.sql.SQLException;

public class FieldAlreadyExistException extends Exception{
    public FieldAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
