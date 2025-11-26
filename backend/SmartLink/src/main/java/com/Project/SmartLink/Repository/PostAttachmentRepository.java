package com.Project.SmartLink.Repository;

import com.Project.SmartLink.entity.Attachment;
import com.Project.SmartLink.entity.Post;
import com.Project.SmartLink.entity.PostAttachmentKey;
import com.Project.SmartLink.entity.PostAttchment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface PostAttachmentRepository extends JpaRepository<PostAttchment, PostAttachmentKey> {
    List<PostAttchment> findAll();
    @Query("SELECT p.id.PostId FROM PostAttchment p WHERE p.id.AttachId = :AttachID")
    List<Long> findPostsByIdOfAttachment(@Param("AttachID") Long AttachID);
    @Query("SELECT p.id.AttachId FROM PostAttchment p WHERE p.id.PostId = :PostId")
    List<Long> findAttachmentsByIdOfPost(@Param("PostId") Long PostId);
    @Modifying
    @Transactional
    @Query("DELETE FROM PostAttchment p WHERE p.id.PostId = :PostId")
    void deletePostById(@Param("PostId") Long PostId);
    @Query("DELETE FROM PostAttchment p WHERE p.id.AttachId = :AttachmentID")
    @Modifying
    @Transactional
    void deleteAttachmentById(@Param("AttachmentID") Long AttachmentID, Long PostId);
    PostAttchment save(PostAttchment postAttchment);
    @Query("UPDATE PostAttchment p SET p.id.PostId = :PostId WHERE p.id.AttachId = :AttachmentID")
    @Modifying
    @Transactional
    void updatePostById(@Param("PostId") Long PostId, @Param("AttachmentID") Long AttachmentID);
    @Query("UPDATE PostAttchment p SET p.id.AttachId = :AttachmentID WHERE p.id.PostId = :PostId")
    @Modifying
    @Transactional
    void updateAttachmentById(@Param("AttachmentID") Long AttachmentID,@Param("PostId") Long PostId);
}
