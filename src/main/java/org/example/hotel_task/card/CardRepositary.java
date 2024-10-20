package org.example.hotel_task.card;

import org.example.hotel_task.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepositary extends JpaRepository<Card, UUID>{
    Optional<Card> findByCardNumber(String cardNumber);
}
