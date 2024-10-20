package org.example.hotel_task.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.hotel_task.card.entity.Card;
import org.example.hotel_task.rend.entity.Rend;
import org.example.hotel_task.user.entity.User;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double price;

    @CreatedDate
    private LocalDateTime paymentDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne(optional = false)
    private Rend rends;
    @Override
    public String toString() {
        return "Payment{" +
                "ID: " + id +
                ", Price: " + price +
                ", Payment Date: " + paymentDate +
                ", User: " + (user != null ? user.getName() : "Unknown User") +
                ", Card: " + (card != null ? card.getCardNumber() : "Unknown Card") +
                "}";
    }

}
