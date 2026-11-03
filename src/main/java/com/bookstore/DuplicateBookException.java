package com.bookstore;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String message) { super(message); }
}
