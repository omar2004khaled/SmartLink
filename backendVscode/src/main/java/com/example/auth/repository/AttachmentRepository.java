package com.example.auth.repository;


import com.example.auth.entity.Attachment;
import com.example.auth.enums.TypeofAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAll();
    Optional<Attachment> findById(Long theId);
    @Modifying
    @Transactional
    void deleteById(Long theId);
    Attachment save(Attachment attachment);
    @Query("SELECT a FROM Attachment a WHERE a.typeofAttachments = :type")
    List<Attachment> findByType(@Param("type") TypeofAttachments type);
    @Modifying
    @Transactional
    @Query("UPDATE Attachment a SET a.attachmentURL = :attachmentURL WHERE a.attachId = :AttachmentID")
    void updateAttachmentById(@Param("attachmentURL") String attachmentURL, @Param("AttachmentID") Long AttachId);

}
