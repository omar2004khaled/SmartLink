package com.example.auth.controller;

import com.example.auth.dto.CommentDTO;
import com.example.auth.exceptions.NonExistentObject;
import com.example.auth.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    CommentService commentService;

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testAddComment_success() throws Exception {
        CommentDTO dto = new CommentDTO();
        dto.setUserId(1L);

        dto.setText("Hello");

        Mockito.when(commentService.addComment(any())).thenReturn(10L);

        mockMvc.perform(post("/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testAddComment_missingTextAndUrl() throws Exception {
        CommentDTO dto = new CommentDTO();


        // No mock setup needed - let validation fail naturally

        mockMvc.perform(post("/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testRemoveComment_success() throws Exception {
        Mockito.when(commentService.removeComment(1L)).thenReturn(true);

        mockMvc.perform(delete("/comment/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testRemoveComment_notFound() throws Exception {
        Mockito.when(commentService.removeComment(1L))
                .thenThrow(new NonExistentObject("Not found"));

        mockMvc.perform(delete("/comment/delete/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testGetAllComments_success() throws Exception {
        CommentDTO dto = new CommentDTO();
        dto.setText("Hello");

        Mockito.when(commentService.getCommentsByPostId(eq(0), eq(10), eq(2L)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/comment/getAll/2/0"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testUpdate_success() throws Exception {
        CommentDTO dto = new CommentDTO();
        dto.setUserId(1L);
        dto.setText("Updated");

        Mockito.when(commentService.update(any()))
                .thenReturn(dto);

        mockMvc.perform(put("/comment/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated"));
    }

    @Test
    @WithMockUser(username = "ali",roles = "USER")
    void testUpdate_notFound() throws Exception {
        Mockito.when(commentService.update(any()))
                .thenThrow(new NonExistentObject("Not found"));

        mockMvc.perform(put("/comment/put")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CommentDTO()))
        ).andExpect(status().is4xxClientError());
    }
}
