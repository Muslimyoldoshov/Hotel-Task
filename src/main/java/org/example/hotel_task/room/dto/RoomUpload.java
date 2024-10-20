package org.example.hotel_task.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomUpload {
    private InputStreamResource resource;
    private String zipFilePath;
}
