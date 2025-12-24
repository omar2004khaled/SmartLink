package com.example.auth.service;

import com.example.auth.dto.ConnectionDto;
import com.example.auth.entity.Connection;
import com.example.auth.entity.Connection.ConnectionStatus;
import com.example.auth.entity.User;
import com.example.auth.repository.ConnectionRepository;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ConnectionService connectionService;

    private User sender;
    private User receiver;
    private Connection connection;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setFullName("John Doe");

        receiver = new User();
        receiver.setId(2L);
        receiver.setFullName("Jane Smith");

        connection = new Connection();
        connection.setId(1L);
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);
    }

    @Test
    void sendRequest_WithValidUsers_ShouldCreateConnection() {
 
        when(connectionRepo.findConnectionBetweenUsers(1L, 2L)).thenReturn(Optional.empty());
        when(userRepo.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepo.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepo.save(any(Connection.class))).thenReturn(connection);


        ConnectionDto result = connectionService.sendRequest(1L, 2L);


        assertNotNull(result);
        assertEquals(1L, result.getSenderId());
        assertEquals(2L, result.getReceiverId());
        verify(connectionRepo).save(any(Connection.class));
    }

    @Test
    void sendRequest_ToSelf_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.sendRequest(1L, 1L));
        assertEquals("can't send connection to yourself", exception.getMessage());
    }

    @Test
    void sendRequest_WithExistingConnection_ShouldThrowException() {
        when(connectionRepo.findConnectionBetweenUsers(1L, 2L)).thenReturn(Optional.of(connection));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.sendRequest(1L, 2L));
        assertEquals("Connection exists", exception.getMessage());
    }

    @Test
    void cancelRequest_WithValidSender_ShouldDeleteConnection() {
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));
        connectionService.cancelRequest(1L, 1L);
        verify(connectionRepo).delete(connection);
    }

    @Test
    void cancelRequest_WithInvalidSender_ShouldThrowException() {
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> connectionService.cancelRequest(1L, 2L));
        assertEquals("Only sender can cancel the request", exception.getMessage());
    }

    @Test
    void acceptRequest_WithValidReceiver_ShouldUpdateStatus() {
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));
        Connection acceptedConnection = new Connection();
        acceptedConnection.setId(1L);
        acceptedConnection.setSender(sender);
        acceptedConnection.setReceiver(receiver);
        acceptedConnection.setStatus(ConnectionStatus.ACCEPTED);
        when(connectionRepo.save(any(Connection.class))).thenReturn(acceptedConnection);
        
        ConnectionDto result = connectionService.acceptRequest(1L, 2L);

        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
        verify(connectionRepo).save(any(Connection.class));
    }

    @Test
    void acceptRequest_WithInvalidReceiver_ShouldThrowException() {
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> connectionService.acceptRequest(1L, 1L));
        assertEquals("receiver only accept the request", exception.getMessage());
    }

    @Test
    void rejectRequest_WithValidReceiver_ShouldUpdateStatus() {
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));
        Connection rejectedConnection = new Connection();
        rejectedConnection.setId(1L);
        rejectedConnection.setSender(sender);
        rejectedConnection.setReceiver(receiver);
        rejectedConnection.setStatus(ConnectionStatus.REJECTED);
        when(connectionRepo.save(any(Connection.class))).thenReturn(rejectedConnection);
        
        ConnectionDto result = connectionService.rejectRequest(1L, 2L);
        
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(connectionRepo).save(any(Connection.class));
    }

    @Test
    void getPendingRequests_ShouldReturnPendingConnections() {
        List<Connection> pendingConnections = Arrays.asList(connection);
        when(connectionRepo.findByUserIdAndStatus(1L, ConnectionStatus.PENDING))
                .thenReturn(pendingConnections);

        List<ConnectionDto> result = connectionService.getPendingRequests(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSenderId());
    }

    @Test
    void getConnections_ShouldReturnAcceptedConnections() {
        connection.setStatus(ConnectionStatus.ACCEPTED);
        List<Connection> acceptedConnections = Arrays.asList(connection);
        when(connectionRepo.findByUserIdAndStatus(1L, ConnectionStatus.ACCEPTED))
                .thenReturn(acceptedConnections);

        List<ConnectionDto> result = connectionService.getConnections(1L);
        assertEquals(1, result.size());
        assertEquals("ACCEPTED", result.get(0).getStatus());
    }

    @Test
    void removeConnection_WithValidUser_ShouldDeleteConnection() {
        connection.setStatus(ConnectionStatus.ACCEPTED);
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));

        connectionService.removeConnection(1L, 1L);
        verify(connectionRepo).delete(connection);
    }

    @Test
    void removeConnection_WithInvalidUser_ShouldThrowException() {
        when(connectionRepo.findById(1L)).thenReturn(Optional.of(connection));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.removeConnection(1L, 3L));
        assertEquals("this user not allowed", exception.getMessage());
    }
}