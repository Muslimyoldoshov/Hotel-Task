package org.example.hotel_task.mail;

import org.example.hotel_task.mail.entity.OTP;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OTPRepository extends CrudRepository<OTP, String> {

}
