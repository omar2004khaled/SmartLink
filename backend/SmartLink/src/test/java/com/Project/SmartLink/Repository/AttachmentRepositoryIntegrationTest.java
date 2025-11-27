package com.Project.SmartLink.Repository;

import com.Project.SmartLink.entity.Attachment;
import com.Project.SmartLink.entity.TypeOfAttachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttachmentRepositoryIntegrationTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    void testSave() {
        Attachment a = new Attachment();
        a.setAttachmentURL("file1.png");
        a.setTypeOfAttachment(TypeOfAttachment.Image);

        Attachment saved = attachmentRepository.save(a);

        assertNotNull(saved.getAttachId());
    }

    @Test
    void testFindById() {
        Attachment a = new Attachment();
        a.setAttachmentURL("mydoc.pdf");
        a.setTypeOfAttachment(TypeOfAttachment.File);
        Attachment saved = attachmentRepository.save(a);

        assertTrue(attachmentRepository.findById(saved.getAttachId()).isPresent());
    }
}
