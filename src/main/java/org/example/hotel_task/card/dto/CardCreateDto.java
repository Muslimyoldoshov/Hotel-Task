package org.example.hotel_task.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotel_task.card.type.CardType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardCreateDto {
    @NotBlank
    @Pattern(regexp = "^(4\\d{12}(?:\\d{3})?|5[1-5]\\d{14}|2[2-7]\\d{14}|3[47]\\d{13}|6(?:011|5\\d{2}|4[4-9]\\d)\\d{12})$")
    private String cardNumber;
    private Double cardBalance;

    private CardType cardType;

    private String expiryDate;
}
