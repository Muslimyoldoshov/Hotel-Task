package org.example.hotel_task.attachment.dto;

import org.example.hotel_task.attachment.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepositary extends JpaRepository<Attachment, UUID> {


}
