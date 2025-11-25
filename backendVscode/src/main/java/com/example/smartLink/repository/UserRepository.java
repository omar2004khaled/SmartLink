package com.example.smartLink.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smartLink.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
