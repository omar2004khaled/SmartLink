package com.example.auth.controller;

import com.example.auth.dto.ConnectionDto;
import com.example.auth.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connections")

public class ConnectionController {
    
    private final ConnectionService connectionService;
    
    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    @PostMapping("/send")
    public ResponseEntity<ConnectionDto> sendRequest(@RequestBody Map<String, Long> request) {
        Long senderId = request.get("senderId");
        Long receiverId = request.get("receiverId");
        return ResponseEntity.ok(connectionService.sendRequest(senderId, receiverId));
    }
    
    @DeleteMapping("/{connectionId}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long connectionId,@RequestParam Long userId) {
        connectionService.cancelRequest(connectionId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{connectionId}/accept")
    public ResponseEntity<ConnectionDto> acceptRequest(@PathVariable Long connectionId, @RequestParam Long userId) {
        return ResponseEntity.ok(connectionService.acceptRequest(connectionId, userId));
    }
    
    @PutMapping("/{connectionId}/reject")
    public ResponseEntity<ConnectionDto> rejectRequest(@PathVariable Long connectionId, @RequestParam Long userId) {
        return ResponseEntity.ok(connectionService.rejectRequest(connectionId, userId));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionDto>> getPendingRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(connectionService.getPendingRequests(userId));
    }
    
    @GetMapping("/accepted")
    public ResponseEntity<List<ConnectionDto>> getConnections(@RequestParam Long userId) {
        return ResponseEntity.ok(connectionService.getConnections(userId));
    }
    
    @DeleteMapping("/{connectionId}/remove")
    public ResponseEntity<Void> removeConnection(@PathVariable Long connectionId, @RequestParam Long userId) {
        connectionService.removeConnection(connectionId, userId);
        return ResponseEntity.noContent().build();
    }
}
