package org.example.hotel_task.hotel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HotelBaseDto {
    @NotBlank
    private String name;
    @NotBlank
    private String location;
    @NotNull
    @Min(value = 1,message = "1 dan 5 bo'lgan sonlar bilan mehmonxonangizni baholang  ")
    @Max(value = 5)
    private int stars;
    @NotNull
    private MultipartFile multipartFile;
}
