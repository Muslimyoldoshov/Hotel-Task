package org.example.hotel_task.rend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RendResponseDto {
    private UUID id;
    private String commentary;
    private String fullPrice;
    private int rate;

}
