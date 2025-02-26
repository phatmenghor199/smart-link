package com.menghor.smart_shop.exceptoins.error;

public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException(String message) {
        super(message);
    }
}
