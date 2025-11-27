package com.example.auth.exceptions;

public class NonExistentObject extends RuntimeException {
    public NonExistentObject(String message) {
        super(message);
    }
}
