// src/test/java/com/example/auth/controller/ReactionControllerTest.java
package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.enums.ReactionType;
import com.example.auth.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ReactionControllerTest {

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private ReactionController reactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


}