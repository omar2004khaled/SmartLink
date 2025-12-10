package com.example.auth.service;
import com.example.auth.dto.ConnectionDto;
import com.example.auth.entity.Connection;
import com.example.auth.entity.Connection.ConnectionStatus;
import com.example.auth.entity.User;
import com.example.auth.repository.ConnectionRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectionService {
    
    private final ConnectionRepository connectionRepo;
    private final UserRepository userRepo;
    
    public ConnectionService(ConnectionRepository connectionRepo, UserRepository userRepo) {
        this.connectionRepo = connectionRepo;
        this.userRepo = userRepo;
    }
    
    @Transactional
    public ConnectionDto sendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) 
            throw new IllegalArgumentException("can't send connection to yourself");
        if (connectionRepo.findConnectionBetweenUsers(senderId, receiverId).isPresent()) 
            throw new IllegalArgumentException("Connection exists");
        
        
        User sender = userRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepo.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        
        Connection saved = connectionRepo.save(connection);
        return toDto(saved);
    }
    
    @Transactional
    public void cancelRequest(Long connectionId, Long userId) {
        Connection connection = connectionRepo.findById(connectionId).orElseThrow(() -> new RuntimeException("Connection not found"));
        
        if (!connection.getSender().getId().equals(userId)) 
            throw new IllegalArgumentException("Only sender can cancel the request");
        if (connection.getStatus() != ConnectionStatus.PENDING) 
            throw new IllegalArgumentException("Can only cancel pending requests");
        
        connectionRepo.delete(connection);
    }
    
    @Transactional
    public ConnectionDto acceptRequest(Long connectionId, Long userId) {
        Connection connection = connectionRepo.findById(connectionId).orElseThrow(() -> new RuntimeException("Connection not found"));
        
        if (!connection.getReceiver().getId().equals(userId))
            throw new IllegalArgumentException("receiver only accept the request");       
        if (connection.getStatus() != ConnectionStatus.PENDING) 
            throw new IllegalArgumentException("Request is not pending");
            
        connection.setStatus(ConnectionStatus.ACCEPTED);
        Connection saved = connectionRepo.save(connection);
        return toDto(saved);
    }
    
    @Transactional
    public ConnectionDto rejectRequest(Long connectionId,Long userId) {
        Connection connection = connectionRepo.findById(connectionId).orElseThrow(() -> new RuntimeException("Connection not found"));
        
        if (!connection.getReceiver().getId().equals(userId)) 
            throw new IllegalArgumentException("receiver only can reject the request");        
        if (connection.getStatus() != ConnectionStatus.PENDING) 
            throw new IllegalArgumentException("Request is not pending");
            
        connection.setStatus(ConnectionStatus.REJECTED);
        Connection saved = connectionRepo.save(connection);
        return toDto(saved);
    }
    
    @Transactional(readOnly = true)
    public List<ConnectionDto> getPendingRequests(Long userId) {
       return connectionRepo.findByUserIdAndStatus(userId, ConnectionStatus.PENDING).stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConnectionDto> getConnections(Long userId) {
        return connectionRepo.findByUserIdAndStatus(userId, ConnectionStatus.ACCEPTED).stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Transactional
    public void removeConnection(Long connectionId, Long userId) {
        Connection connection = connectionRepo.findById(connectionId).orElseThrow(() -> new RuntimeException("connection not found"));
        
        if (!connection.getSender().getId().equals(userId) && !connection.getReceiver().getId().equals(userId)) 
            throw new IllegalArgumentException("this user not allowed");
        
       connectionRepo.delete(connection);
    }
    
    private ConnectionDto toDto(Connection connection) {
        ConnectionDto dto = new ConnectionDto();
            dto.setId(connection.getId());
            dto.setSenderId(connection.getSender().getId());
            dto.setSenderName(connection.getSender().getFullName());
            dto.setReceiverId(connection.getReceiver().getId());
            dto.setReceiverName(connection.getReceiver().getFullName());
            dto.setCreatedAt(connection.getCreatedAt());
            dto.setStatus(connection.getStatus().name());
        return dto;
    }
}
