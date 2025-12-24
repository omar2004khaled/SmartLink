package com.example.auth.repository;

import com.example.auth.entity.Connection;
import com.example.auth.entity.Connection.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    
    @Query("""
            SELECT c FROM Connection c 
            WHERE (c.sender.id =?1OR c.receiver.id =?1)
            AND c.status =?2
            """)
    List<Connection> findByUserIdAndStatus(Long userId, ConnectionStatus status);
    
    @Query("""
            SELECT c FROM Connection c 
            WHERE c.receiver.id =?1 
            AND c.status='PENDING'
            """)
    List<Connection> findPendingRequestsByReceiverId(Long receiverId);
    
    @Query(""" 
            SELECT c FROM Connection c 
            WHERE c.sender.id =?1 
            AND c.receiver.id =?2
            """)
    Optional<Connection> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    
    @Query("""
            SELECT c FROM Connection c 
            WHERE ((c.sender.id =?1AND c.receiver.id =?2)
            OR(c.sender.id =?2AND c.receiver.id =?1))
            AND c.status = 'ACCEPTED'            
            """)
    Optional<Connection> findConnectionBetweenUsers(Long userId1, Long userId2);

    @Modifying
    @Transactional
    void deleteBySender_Id(Long senderId);

    @Modifying
    @Transactional
    void deleteByReceiver_Id(Long receiverId);
}
