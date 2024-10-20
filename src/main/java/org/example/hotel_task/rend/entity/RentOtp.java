package org.example.hotel_task.rend.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash(timeToLive = 600)
public class RentOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID roomId;
    private String commentary;
    private int rate;
    private LocalDate startTime;
    private LocalDate endTime;

    @Override
    public String toString() {
        return "RentOtp{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", commentary='" + commentary + '\'' +
                ", rate=" + rate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
