package com.example.auth.controller;

import com.example.auth.exceptions.NotEnoughInformationException;
import com.example.auth.service.LikeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LikeCommentControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LikeService likeService;

    @Test
    @WithMockUser(username = "ali", roles = "USER")
    void testLikeComment_success() throws Exception {
        Mockito.doNothing().when(likeService).makeOrDeleteLike(1L, 4L);

        mockMvc.perform(get("/like/1/4"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ali", roles = "USER")
    void testLikeComment_userNotFound() throws Exception {
        Mockito.doThrow(new NotEnoughInformationException("There is no such user"))
                .when(likeService).makeOrDeleteLike(999L, 4L);

        mockMvc.perform(get("/like/999/4"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "ali", roles = "USER")
    void testLikeComment_commentNotFound() throws Exception {
        Mockito.doThrow(new NotEnoughInformationException("There is no such comment"))
                .when(likeService).makeOrDeleteLike(1L, 999L);

        mockMvc.perform(get("/like/1/999"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser(username = "ali", roles = "USER")
    void testLikeComment_multipleOperations() throws Exception {
        Mockito.doNothing().when(likeService).makeOrDeleteLike(1L, 4L);

        mockMvc.perform(get("/like/1/4"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/like/1/4"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/like/1/4"))
                .andExpect(status().isOk());
    }

}