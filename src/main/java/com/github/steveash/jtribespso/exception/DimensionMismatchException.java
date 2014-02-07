package com.github.steveash.jtribespso.exception;

public class DimensionMismatchException extends RuntimeException {

    public DimensionMismatchException() {
    }

    public DimensionMismatchException(String message) {
        super(message);
    }

    public DimensionMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public DimensionMismatchException(Throwable cause) {
        super(cause);
    }
}

