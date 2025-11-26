package com.example.smartLink.exceptions;

public class NonExistentObject extends RuntimeException {
    public NonExistentObject(String message) {
        super(message);
    }
}
