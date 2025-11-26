package com.example.smartLink.repository;

import com.example.smartLink.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttacchmentsRepository extends JpaRepository<Attachment,Long> {
}
