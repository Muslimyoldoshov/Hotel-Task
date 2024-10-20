package org.example.hotel_task.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomUpdateDto {
    @NotBlank
    private String number;
    @NotNull
    private int capacity;
    @NotNull
    private Double price;
    @NotNull
    private UUID roomTypeId;
    @NotNull
    private UUID hotelId;
    @NotNull
    private MultipartFile multipartFile;
}
