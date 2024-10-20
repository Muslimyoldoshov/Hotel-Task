package org.example.hotel_task.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotel_task.card.type.CardType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardUpdateDto {
    private String cardNumber;
    private Double cardBalance;

    private CardType cardType;

    private String expiryDate;
}
