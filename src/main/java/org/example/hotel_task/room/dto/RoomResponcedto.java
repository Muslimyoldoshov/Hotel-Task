package org.example.hotel_task.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotel_task.attachment.dto.AttachmentResponceDto;
import org.example.hotel_task.roomtype.entity.RoomType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomResponcedto {
    private UUID id;
    private String number;
    private int capacity;
    private double price;
    private RoomType roomType;
    private AttachmentResponceDto attachment;
}
