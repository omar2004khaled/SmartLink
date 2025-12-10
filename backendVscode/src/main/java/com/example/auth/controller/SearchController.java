package com.example.auth.controller;

import com.example.auth.dto.UserSearchDTO;
import com.example.auth.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/users")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Query parameter is required");
                return ResponseEntity.badRequest().body(error);
            }

            if (query.length() < 2) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Query must be at least 2 characters");
                return ResponseEntity.badRequest().body(error);
            }

            List<UserSearchDTO> results = searchService.searchUsers(query);
            
            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("count", results.size());
            response.put("results", results);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while searching");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
