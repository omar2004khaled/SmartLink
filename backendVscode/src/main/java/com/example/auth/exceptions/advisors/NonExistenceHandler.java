package com.example.auth.exceptions.advisors;

import com.example.auth.exceptions.NonExistentObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class NonExistenceHandler {
    @ExceptionHandler(NonExistentObject.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    Map<String, String> onDataNotFoundException(NonExistentObject exception) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", exception.getMessage());
        return error;
    }
}
