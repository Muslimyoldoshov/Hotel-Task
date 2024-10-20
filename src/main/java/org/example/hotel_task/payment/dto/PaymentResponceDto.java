package org.example.hotel_task.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentResponceDto {
    private UUID id;
    private double price;
    private LocalDateTime paymentDate;
}
