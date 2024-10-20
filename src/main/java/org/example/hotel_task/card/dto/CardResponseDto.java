package org.example.hotel_task.card.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardResponseDto {
    private UUID id;
    private String cardNumber;
    private Double cardBalance;
    private String cardHolderName;
    private String expiryDate;

}
