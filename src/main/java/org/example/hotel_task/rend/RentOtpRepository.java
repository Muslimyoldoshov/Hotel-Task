package org.example.hotel_task.rend;

import org.example.hotel_task.rend.entity.RentOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RentOtpRepository extends CrudRepository<RentOtp, UUID> {
  RentOtp findByRoomId(UUID roomId);
}
