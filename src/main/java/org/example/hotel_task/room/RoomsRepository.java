package org.example.hotel_task.room;

import org.example.hotel_task.room.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomsRepository extends JpaRepository<Rooms, UUID> , JpaSpecificationExecutor<Rooms> {

}
