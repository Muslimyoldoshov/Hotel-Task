package org.example.hotel_task.rend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hotel_task.mail.OTPRepository;
import org.example.hotel_task.payment.PaymentRepositary;
import org.example.hotel_task.rend.dto.RendResponseDto;
import org.example.hotel_task.rend.dto.RentCreateDto;
import org.example.hotel_task.rend.entity.Rend;
import org.example.hotel_task.rend.entity.RentOtp;
import org.example.hotel_task.room.entity.Rooms;
import org.example.hotel_task.room.RoomsRepository;
import org.example.hotel_task.user.UserRepository;
import org.example.hotel_task.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RendService {
    private final RendRepository rendRepository;
    private final UserRepository userRepository;
    private final RoomsRepository roomsRepository;
    private final PaymentRepositary paymentRepositary;
    private final RentOtpRepository rentOtpRepository;
    private final OTPRepository otpRepository;

    private final ModelMapper mapper = new ModelMapper();

    public RendResponseDto createRent(RentCreateDto rentCreateDto, LocalDate startTime, LocalDate endTime) throws CustomErrorResponse {
        try {
            Optional<Rooms> optionalRoom = roomsRepository.findById(rentCreateDto.getRoomId());
            if (optionalRoom.isEmpty()) {
                throw new CustomErrorResponse("Room not found");
            }

            Rooms rooms = optionalRoom.get();
            otpRepository.deleteAll();
            List<Rend> existingRents = rendRepository.findByRoomsId(rentCreateDto.getRoomId());
            if (!isRoomAvailable(existingRents, startTime, endTime)) {
                throw new CustomErrorResponse("Room is already booked");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findUserByEmail(email).orElseThrow(() -> new CustomErrorResponse("User not found"));

            long daysBetween = ChronoUnit.DAYS.between(startTime, endTime);
            RentOtp rentOtp = mapper.map(rentCreateDto, RentOtp.class);
            rentOtp.setId(UUID.randomUUID());
            rentOtp.setRoomId(rooms.getId());
            rentOtp.setStartTime(startTime);
            rentOtp.setEndTime(endTime);
            RentOtp savedRentOtp = rentOtpRepository.save(rentOtp);
            RendResponseDto responseDto = mapper.map(savedRentOtp, RendResponseDto.class);
            responseDto.setFullPrice("Full price for the days booked: " + (daysBetween * rooms.getPrice()) + " so'm. " +
                    "Note: You can only confirm the booking after making the payment.");

            return responseDto;
        } catch (CustomErrorResponse e) {
            throw new CustomErrorResponse("Error creating rent: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating rent: " + e.getMessage(), e);
        }
    }

    private boolean isRoomAvailable(List<Rend> existingRents, LocalDate startTime, LocalDate endTime) throws CustomErrorResponse {
        try {
            if (startTime.isAfter(endTime)) {
                throw new CustomErrorResponse("Start date cannot be after the end date.");
            }

            if (startTime.isBefore(LocalDate.now())) {
                throw new CustomErrorResponse("Start date must be after today.");
            }
            for (Rend existingRent : existingRents) {
                if (datesOverlap(startTime, endTime, existingRent.getStartTime(), existingRent.getEndTime())) {
                    throw new CustomErrorResponse("This room is already booked for the selected dates.");
                }
            }
            return true;
        } catch (CustomErrorResponse e) {
            throw new CustomErrorResponse("Error checking room availability: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unknown error checking room availability: " + e.getMessage(), e);
        }
    }

    private boolean datesOverlap(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        return !startDate1.isAfter(endDate2) && !startDate2.isAfter(endDate1);
    }

    @Getter
    public static class CustomErrorResponse extends Exception {
        private final RuntimeException status =new RuntimeException();

        public CustomErrorResponse(String message) {
            super(message);
        }

    }
}
