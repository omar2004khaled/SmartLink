package com.example.auth.dto.CompanyTests;

import com.example.auth.dto.FollowRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
@AutoConfigureMockMvc
@SpringBootTest
class FollowRequestTest {

    @Test
    void testFollowRequestCreation() {
        FollowRequest request = new FollowRequest();
        request.setUserId(100L);

        assertEquals(100L, request.getUserId());
    }

    @Test
    void testFollowRequestAllArgsConstructor() {
        FollowRequest request = new FollowRequest(100L);

        assertEquals(100L, request.getUserId());
    }

    @Test
    void testFollowRequestNoArgsConstructor() {
        FollowRequest request = new FollowRequest();

        assertNull(request.getUserId());
    }

    @Test
    void testFollowRequestSetter() {
        FollowRequest request = new FollowRequest();
        request.setUserId(200L);

        assertEquals(200L, request.getUserId());
    }
}
