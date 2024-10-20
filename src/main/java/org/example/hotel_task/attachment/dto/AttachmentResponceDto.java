package org.example.hotel_task.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentResponceDto {
    private UUID attachment_id;
    private String fileName;
    private String fileType;
    private String url;
    private LocalDateTime uploadTime;
}
