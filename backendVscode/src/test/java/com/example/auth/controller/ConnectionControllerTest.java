package com.example.auth.controller;

import com.example.auth.dto.ConnectionDto;
import com.example.auth.service.ConnectionService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConnectionControllerTest {

    @Mock
    private ConnectionService connectionService;

    @InjectMocks
    private ConnectionController connectionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ConnectionDto connectionDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(connectionController).build();
        objectMapper = new ObjectMapper();
        
        connectionDto = new ConnectionDto();
        connectionDto.setId(1L);
        connectionDto.setSenderId(1L);
        connectionDto.setSenderName("John Doe");
        connectionDto.setReceiverId(2L);
        connectionDto.setReceiverName("Jane Smith");
        connectionDto.setStatus("PENDING");
        connectionDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void sendRequest_WithValidData_ShouldReturnConnection() throws Exception {
        Map<String, Long> request = new HashMap<>();
        request.put("senderId", 1L);
        request.put("receiverId", 2L);
        
        when(connectionService.sendRequest(1L, 2L)).thenReturn(connectionDto);
        
        mockMvc.perform(post("/api/connections/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.senderId").value(1L))
                .andExpect(jsonPath("$.receiverId").value(2L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(connectionService).sendRequest(1L, 2L);
    }

    @Test
    void cancelRequest_WithValidData_ShouldReturnNoContent() throws Exception {
        doNothing().when(connectionService).cancelRequest(1L, 1L);
        
        mockMvc.perform(delete("/api/connections/1/cancel")
                .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(connectionService).cancelRequest(1L, 1L);
    }

    @Test
    void acceptRequest_WithValidData_ShouldReturnConnection() throws Exception {
        connectionDto.setStatus("ACCEPTED");
        when(connectionService.acceptRequest(1L, 2L)).thenReturn(connectionDto);
        
        mockMvc.perform(put("/api/connections/1/accept")
                .param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(connectionService).acceptRequest(1L, 2L);
    }

    @Test
    void rejectRequest_WithValidData_ShouldReturnConnection() throws Exception {
        connectionDto.setStatus("REJECTED");
        when(connectionService.rejectRequest(1L, 2L)).thenReturn(connectionDto);

        mockMvc.perform(put("/api/connections/1/reject")
                .param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(connectionService).rejectRequest(1L, 2L);
    }

    @Test
    void getPendingRequests_WithValidUserId_ShouldReturnConnections() throws Exception {
        List<ConnectionDto> connections = Arrays.asList(connectionDto);
        when(connectionService.getPendingRequests(1L)).thenReturn(connections);

        mockMvc.perform(get("/api/connections/pending")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(connectionService).getPendingRequests(1L);
    }

    @Test
    void getConnections_WithValidUserId_ShouldReturnConnections() throws Exception {
        connectionDto.setStatus("ACCEPTED");
        List<ConnectionDto> connections = Arrays.asList(connectionDto);
        when(connectionService.getConnections(1L)).thenReturn(connections);

        mockMvc.perform(get("/api/connections/accepted")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("ACCEPTED"));

        verify(connectionService).getConnections(1L);
    }

    @Test
    void removeConnection_WithValidData_ShouldReturnNoContent() throws Exception {
        doNothing().when(connectionService).removeConnection(1L, 1L);

        mockMvc.perform(delete("/api/connections/1/remove")
                .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(connectionService).removeConnection(1L, 1L);
    }
}