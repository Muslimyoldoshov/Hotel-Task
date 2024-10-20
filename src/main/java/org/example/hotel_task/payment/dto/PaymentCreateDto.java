package org.example.hotel_task.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentCreateDto {
    @NotNull
    private UUID card_Id;
    @NotNull
    private double price;
}
