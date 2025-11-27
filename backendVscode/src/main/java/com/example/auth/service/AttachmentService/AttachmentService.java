package com.example.auth.service.AttachmentService;

import com.example.auth.entity.Attachment;
import com.example.auth.enums.TypeofAttachments;  // Note: Fixed enum name to match your actual enum
import java.util.List;
import java.util.Optional;

public interface AttachmentService {
    List<Attachment> findAll();
    Optional<Attachment> findById(Long theId);
    void deleteById(Long theId);
    Attachment save(Attachment attachment);
    List<Attachment> findByType(TypeofAttachments type);
    void updateAttachmentById(Attachment attachment);
}
