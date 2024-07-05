package ru.discomfortDeliverer.exceptions;

public class DataBaseAccessException extends Exception{
    public DataBaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
