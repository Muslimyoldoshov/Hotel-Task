package org.example.hotel_task.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotel_task.attachment.dto.AttachmentResponceDto;
import org.example.hotel_task.room.entity.Rooms;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HotelResponceDto {
    private UUID uuid;
    private String name;
    private String location;
    private int stars;
    private List<Rooms> rooms;
    private AttachmentResponceDto attachment;
}
