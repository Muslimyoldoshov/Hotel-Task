package org.example.hotel_task.hotel;

import org.example.hotel_task.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelRepositary extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {
}
