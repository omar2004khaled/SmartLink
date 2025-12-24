package com.example.auth.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.auth.entity.VerificationToken;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    @jakarta.transaction.Transactional
    void deleteByUser_Id(Long userId);
}
