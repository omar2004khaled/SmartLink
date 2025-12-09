package com.example.auth.service.AttachmentService;
import com.example.auth.entity.Attachment;
import com.example.auth.enums.TypeofAttachments;

import com.example.auth.entity.PostAttchment;
import com.example.auth.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class AttachmentSericeImp implements AttachmentService {
    private AttachmentRepository attachmentRepository;
    @Autowired
    public AttachmentSericeImp(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }
    @Override
    public List<Attachment> findAll() {  // for profile to show photos posted of specific user
        return attachmentRepository.findAll();
    }

    @Override
    public Optional<Attachment> findById(Long theId) {
        return attachmentRepository.findById(theId);
    }

    @Override
    public void deleteById(Long theId) {
        attachmentRepository.deleteById(theId);
    }

    @Override
    public Attachment save(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }


    @Override
    public void updateAttachmentById(Attachment attach) {
        attachmentRepository.updateAttachmentById(attach.getAttachmentURL(), attach.getAttachId().longValue());
    }
}
