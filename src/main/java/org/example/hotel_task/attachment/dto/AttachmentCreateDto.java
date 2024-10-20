package org.example.hotel_task.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentCreateDto {
    private String fileName;

    private String fileType;

    private String url;

    private LocalDateTime uploadTime;
}
