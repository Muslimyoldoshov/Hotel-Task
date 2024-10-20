
package org.example.hotel_task.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotel_task.card.CardRepositary;
import org.example.hotel_task.card.entity.Card;
import org.example.hotel_task.pagelmpl.SpecificationBuilder;
import org.example.hotel_task.payment.dto.PaymentCreateDto;
import org.example.hotel_task.payment.dto.PaymentResponceDto;
import org.example.hotel_task.payment.entity.Payment;
import org.example.hotel_task.rend.RendRepository;
import org.example.hotel_task.rend.RentOtpRepository;
import org.example.hotel_task.rend.entity.Rend;
import org.example.hotel_task.rend.entity.RentOtp;
import org.example.hotel_task.room.entity.Rooms;
import org.example.hotel_task.room.RoomsRepository;
import org.example.hotel_task.user.UserRepository;
import org.example.hotel_task.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepositary paymentRepositary;
    private final UserRepository userRepository;
    private final RendRepository rendRepository;
    private final RoomsRepository roomsRepository;
    private final RentOtpRepository rentOtpRepository;
    private final CardRepositary cardRepositary;
    private final ModelMapper mapper = new ModelMapper();

    public PaymentResponceDto save(UUID roomId, PaymentCreateDto paymentCreateDto) {

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findUserByEmail(authentication.getName()).get();
        Rooms rooms = roomsRepository.findById(roomId).get();
        RentOtp byRoomId = rentOtpRepository.findByRoomId(roomId);
        Card byCards = userRepository.findByCards(paymentCreateDto.getCard_Id());

        byCards.setCardBalance(byCards.getCardBalance()-paymentCreateDto.getPrice());
        Card save2 = cardRepositary.save(byCards);

        Rend map1 = mapper.map(byRoomId, Rend.class);
        Rend save = rendRepository.save(map1);

        Payment payment=new Payment(UUID.randomUUID(), paymentCreateDto.getPrice(), LocalDateTime.now(),user,save2,save);
        Payment save1 = paymentRepositary.save(payment);
        return mapper.map(save1,PaymentResponceDto.class);

    }

    public List<PaymentResponceDto> getAllPay(Pageable pageable, String predicate) {
        try {
            Specification<Payment> specification = SpecificationBuilder.build(predicate);
            if (specification == null) {
                return paymentRepositary.findAll(pageable)
                        .map(payment -> mapper.map(payment, PaymentResponceDto.class))
                        .toList();
            }
            return paymentRepositary.findAll(specification, pageable)
                    .map(payment -> mapper.map(payment, PaymentResponceDto.class))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching payment records: " + e.getMessage(), e);
        }
    }

    public PaymentResponceDto getByID(UUID id) {
        try {
            Payment payment = paymentRepositary.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            return mapper.map(payment, PaymentResponceDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching payment by ID: " + e.getMessage(), e);
        }
    }
}
