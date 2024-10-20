package org.example.hotel_task.rend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RentUploadDto {
    private UUID id;
    private String commentary;
    private LocalDate startDate;
    private LocalDate endDate;
    private String fullPrice;
    private int rate;
    private String room_number;
}
