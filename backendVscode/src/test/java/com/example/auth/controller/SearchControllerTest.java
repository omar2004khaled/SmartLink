package com.example.auth.controller;

import com.example.auth.dto.UserSearchDTO;
import com.example.auth.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void searchUsers_ValidQuery_ReturnsResults() throws Exception {
        String query = "john";
        Long currentUserId = 1L;
        List<UserSearchDTO> mockResults = Arrays.asList(
            new UserSearchDTO(2L, "John Doe", "john.doe@email.com", "123456789"),
            new UserSearchDTO(3L, "Johnny Smith", "johnny.smith@email.com", "987654321")
        );
        
        when(searchService.searchUsers(query, currentUserId)).thenReturn(mockResults);


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value(query))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].id").value(2))
                .andExpect(jsonPath("$.results[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.results[0].email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.results[1].id").value(3))
                .andExpect(jsonPath("$.results[1].fullName").value("Johnny Smith"));

        verify(searchService, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_EmptyResults_ReturnsEmptyArray() throws Exception {

        String query = "nonexistent";
        Long currentUserId = 1L;
        
        when(searchService.searchUsers(query, currentUserId)).thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value(query))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results").isEmpty());

        verify(searchService, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_NullQuery_ReturnsBadRequest() throws Exception {

        Long currentUserId = 1L;


        mockMvc.perform(get("/api/search/users")
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Query parameter is required"));

        verify(searchService, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_EmptyQuery_ReturnsBadRequest() throws Exception {

        String query = "";
        Long currentUserId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Query parameter is required"));

        verify(searchService, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_WhitespaceQuery_ReturnsBadRequest() throws Exception {

        String query = "   ";
        Long currentUserId = 1L;


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Query parameter is required"));

        verify(searchService, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_ShortQuery_ReturnsBadRequest() throws Exception {

        String query = "a";
        Long currentUserId = 1L;


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Query must be at least 2 characters"));

        verify(searchService, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_ServiceThrowsIllegalArgumentException_ReturnsBadRequest() throws Exception {

        String query = "test";
        Long currentUserId = 1L;
        String errorMessage = "Invalid search parameters";
        
        when(searchService.searchUsers(query, currentUserId))
            .thenThrow(new IllegalArgumentException(errorMessage));


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));

        verify(searchService, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {

        String query = "test";
        Long currentUserId = 1L;
        String errorMessage = "Database connection failed";
        
        when(searchService.searchUsers(query, currentUserId))
            .thenThrow(new RuntimeException(errorMessage));


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An error occurred while searching"))
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(searchService, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_MissingCurrentUserId_ReturnsBadRequest() throws Exception {

        String query = "test";


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(searchService, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_ValidTwoCharacterQuery_ReturnsResults() throws Exception {

        String query = "jo";
        Long currentUserId = 1L;
        List<UserSearchDTO> mockResults = Arrays.asList(
            new UserSearchDTO(2L, "John Doe", "john.doe@email.com", "123456789")
        );
        
        when(searchService.searchUsers(query, currentUserId)).thenReturn(mockResults);


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value(query))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].fullName").value("John Doe"));

        verify(searchService, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_LongQuery_ReturnsResults() throws Exception {

        String query = "this is a very long search query with many words";
        Long currentUserId = 1L;
        List<UserSearchDTO> mockResults = Collections.emptyList();
        
        when(searchService.searchUsers(query, currentUserId)).thenReturn(mockResults);


        mockMvc.perform(get("/api/search/users")
                .param("query", query)
                .param("currentUserId", currentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value(query))
                .andExpect(jsonPath("$.count").value(0));

        verify(searchService, times(1)).searchUsers(query, currentUserId);
    }
}