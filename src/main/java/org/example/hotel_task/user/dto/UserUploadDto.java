package org.example.hotel_task.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUploadDto {
    private InputStreamResource resource;
    private String zipFilePath;
}
