package com.example.smartLink.exceptions.advisors;

import com.example.smartLink.exceptions.NotEnoughInformationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class DataNotEnoughHandler {
    @ExceptionHandler(NotEnoughInformationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Map<String, String> onDataNotFoundException(NotEnoughInformationException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", exception.getMessage());
        return error;
    }
}
