package org.example.hotel_task.payment;

import org.example.hotel_task.payment.entity.Payment;
import org.example.hotel_task.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepositary extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

}
