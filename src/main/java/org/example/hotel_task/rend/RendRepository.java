package org.example.hotel_task.rend;

import org.example.hotel_task.rend.entity.Rend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RendRepository extends JpaRepository<Rend, UUID> {
    List<Rend>findByRoomsId(UUID roomsId);
}
