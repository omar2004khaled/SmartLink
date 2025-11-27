package com.example.auth.repository;

import com.example.auth.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttacchmentsRepository extends JpaRepository<Attachment,Long> {
}
