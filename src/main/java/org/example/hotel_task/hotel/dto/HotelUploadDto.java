package org.example.hotel_task.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HotelUploadDto {
    private InputStreamResource resource;
    private String zipFilePath;
}
