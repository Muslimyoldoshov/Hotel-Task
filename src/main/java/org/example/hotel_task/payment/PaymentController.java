package org.example.hotel_task.payment;

import lombok.RequiredArgsConstructor;
import org.example.hotel_task.payment.dto.PaymentCreateDto;
import org.example.hotel_task.payment.dto.PaymentResponceDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/{roomId}")
    public ResponseEntity<PaymentResponceDto>create(@PathVariable UUID roomId, @RequestBody PaymentCreateDto paymentCreateDto) throws InsufficientResourcesException {
        PaymentResponceDto paymentResponceDto=paymentService.save(roomId,paymentCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponceDto);
    }
    @GetMapping
    public ResponseEntity<List<PaymentResponceDto>>getAll(Pageable pageable,@RequestParam(required = false)String predicate){
        List<PaymentResponceDto>paymentResponceDtos=paymentService.getAllPay(pageable,predicate);
        return ResponseEntity.ok().body(paymentResponceDtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponceDto>getById(@PathVariable UUID id){
        PaymentResponceDto paymentResponceDto=paymentService.getByID(id);
        return ResponseEntity.ok(paymentResponceDto);
    }
}
