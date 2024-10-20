package org.example.hotel_task.roomtype;

import org.example.hotel_task.roomtype.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomTypeRepositary extends JpaRepository<RoomType, UUID> {
}
