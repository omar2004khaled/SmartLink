package com.example.auth.service;

import com.example.auth.dto.UserSearchDTO;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private UserRepository userRepository;

    public List<UserSearchDTO> searchUsers(String query, Long currentUserId) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        List<User> users = userRepository.searchUsers(query.trim(), currentUserId);
        
        return users.stream()
                .map(user -> new UserSearchDTO(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }
}
