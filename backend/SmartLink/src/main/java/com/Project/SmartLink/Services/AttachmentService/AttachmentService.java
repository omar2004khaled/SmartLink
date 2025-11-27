package com.Project.SmartLink.Services.AttachmentService;

import com.Project.SmartLink.entity.Attachment;
import com.Project.SmartLink.entity.TypeOfAttachment;


import java.util.List;
import java.util.Optional;

public interface AttachmentService {
    List<Attachment> findAll();
    Optional<Attachment> findById(Long theId);
    void deleteById(Long theId);
    Attachment save(Attachment attachment);
    List<Attachment> findByType(TypeOfAttachment type);
    void updateAttachmentById(Attachment attachment);
}
