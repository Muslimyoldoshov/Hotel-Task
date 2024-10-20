package org.example.hotel_task.card;

import lombok.RequiredArgsConstructor;
import org.example.hotel_task.card.dto.CardCreateDto;
import org.example.hotel_task.card.dto.CardResponseDto;
import org.example.hotel_task.card.dto.CardUpdateDto;
import org.example.hotel_task.card.entity.Card;
import org.example.hotel_task.pagelmpl.SpecificationBuilder;
import org.example.hotel_task.user.UserRepository;
import org.example.hotel_task.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepositary cardRepositary;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public CardResponseDto saveCard(CardCreateDto cardCreateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Card newCard = modelMapper.map(cardCreateDto, Card.class);
            newCard.setUsers(user);
            user.getCards().add(newCard);

            Card savedCard = cardRepositary.save(newCard);
            CardResponseDto map = modelMapper.map(savedCard, CardResponseDto.class);
            map.setCardHolderName(user.getName());
            map.setCardNumber(cutCardNumber(savedCard.getCardNumber()));
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Error saving card: " + e.getMessage(), e);
        }
    }

    public List<CardResponseDto> findAll() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return cardRepositary.findAll()
                    .stream()
                    .filter(card -> card.getUsers().getEmail().equals(authentication.getName()))
                    .map(card -> modelMapper.map(card, CardResponseDto.class)).toList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving cards: " + e.getMessage(), e);
        }
    }

    public String cutCardNumber(String cardNumber) {
        try {
            String lastTwoDigits = cardNumber.substring(cardNumber.length() - 4);
            String maskedPart = "*".repeat(cardNumber.length() - 4);
            return maskedPart + lastTwoDigits;
        } catch (Exception e) {
            throw new RuntimeException("Error masking card number: " + e.getMessage(), e);
        }
    }

    public CardResponseDto findById(UUID id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Card card = cardRepositary.findById(id)
                    .orElseThrow(() -> new NotFoundException("Card not found"));

            if (authentication.getName().equals(card.getUsers().getEmail())) {
                return modelMapper.map(card, CardResponseDto.class);
            }
            throw new NotFoundException("This card does not belong to you!");
        } catch (NotFoundException e) {
            throw new NotFoundException("Card not found or it does not belong to you: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving card information: " + e.getMessage(), e);
        }
    }

    public void deleteCard(UUID id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Card card = cardRepositary.findById(id)
                    .orElseThrow(() -> new NotFoundException("Card not found"));

            if (authentication.getName().equals(card.getUsers().getEmail())) {
                cardRepositary.deleteById(id);
            } else {
                throw new RuntimeException("This card does not belong to you!");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error deleting card: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unknown error deleting card: " + e.getMessage(), e);
        }
    }

    public CardResponseDto update(UUID id, CardUpdateDto cardUpdateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Card card = cardRepositary.findById(id)
                    .orElseThrow(() -> new NotFoundException("Card not found"));

            if (authentication.getName().equals(card.getUsers().getEmail())) {
                modelMapper.map(cardUpdateDto, card);
                Card savedCard = cardRepositary.save(card);
                return modelMapper.map(savedCard, CardResponseDto.class);
            }
            throw new RuntimeException("This card does not belong to you!");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error updating card: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unknown error updating card: " + e.getMessage(), e);
        }
    }
}
