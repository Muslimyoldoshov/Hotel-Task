package org.example.hotel_task.card.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.hotel_task.card.type.CardType;
import org.example.hotel_task.user.entity.User;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "cards")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "card_number")
})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(min = 16, max = 16, message = "Karta raqami 16 ta raqamdan iborat bo'lishi kerak")
    @Column(unique = true, nullable = false,name = "card_number")
    private String cardNumber;

    private Double cardBalance;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @NotBlank
    private String expiryDate;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private User users;
}
