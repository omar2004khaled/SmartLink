package com.Project.SmartLink;

import com.Project.SmartLink.REST.PostRestController;
import com.Project.SmartLink.DTO.PostDTO;
import com.Project.SmartLink.Services.AttachmentService.AttachmentService;
import com.Project.SmartLink.Services.PostAttachmentService.PostAttachmentService;
import com.Project.SmartLink.Services.PostService.PostService;
import com.Project.SmartLink.entity.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostRestControllerIntegrationTest {

    private MockMvc mockMvc;

    private PostService postService = Mockito.mock(PostService.class);
    private AttachmentService attachmentService = Mockito.mock(AttachmentService.class);
    private PostAttachmentService postAttachmentService = Mockito.mock(PostAttachmentService.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        PostRestController controller =
                new PostRestController(postService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testFindAll() throws Exception {
        Post post = new Post(1L, "Hello world");
        post.setPostId(1L);
        Page<Post> postsPage = new PageImpl<>(List.of(post));
        when(postService.findAll(any(Pageable.class))).thenReturn(postsPage);
        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(List.of());

        mockMvc.perform(get("/Post/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddPost() throws Exception {
        PostDTO dto = new PostDTO(1L, "content", 1L, new ArrayList<>(), new Timestamp(System.currentTimeMillis()));
        PostDTO saved = new PostDTO(dto.getContent(),dto.getUserId(),   dto.getAttachments(), dto.getCreatedAt());
        saved.setId(10L);

        when(postService.save(any())).thenReturn(saved);

        mockMvc.perform(post("/Post/add")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
