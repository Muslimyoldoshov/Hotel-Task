package org.example.hotel_task.card;

import lombok.RequiredArgsConstructor;
import org.example.hotel_task.card.dto.CardCreateDto;
import org.example.hotel_task.card.dto.CardResponseDto;
import org.example.hotel_task.card.dto.CardUpdateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    @PostMapping
    public ResponseEntity<CardResponseDto>create(@RequestBody CardCreateDto cardCreateDto){
       CardResponseDto cardResponseDto= cardService.saveCard(cardCreateDto);
       return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }
    @GetMapping
    public ResponseEntity<List<CardResponseDto>>getAll(Pageable pageable, @RequestParam(required = false) String predicate){
        List<CardResponseDto>cardResponseDtos=cardService.findAll();
        return ResponseEntity.ok(cardResponseDtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto>getById(@PathVariable UUID id){
        CardResponseDto cardResponseDto=cardService.findById(id);
        return ResponseEntity.ok(cardResponseDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?>delete(@PathVariable UUID id){
        cardService.deleteCard(id);
        return ResponseEntity.ok("");
    }
    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDto>put(@PathVariable UUID id,@RequestBody CardUpdateDto cardUpdateDto){
       CardResponseDto cardResponseDto= cardService.update(id,cardUpdateDto);
       return ResponseEntity.ok(cardResponseDto);
    }

}
