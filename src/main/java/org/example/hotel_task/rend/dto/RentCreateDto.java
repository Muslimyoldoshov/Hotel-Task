package org.example.hotel_task.rend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RentCreateDto {
    @NotNull
    private UUID roomId;
    private String commentary;
    @NotNull
    @Min(value = 1,message = "1 dan 5 gacha bo'lgan tartibda baholang")
    @Max(value =5,message = "1 dan 5 gacha bolgan tartibda baholang")
    private int rate;
}
