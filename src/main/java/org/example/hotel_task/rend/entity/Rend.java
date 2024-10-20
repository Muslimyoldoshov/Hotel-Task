package org.example.hotel_task.rend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.hotel_task.payment.entity.Payment;
import org.example.hotel_task.room.entity.Rooms;
import org.example.hotel_task.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Rend {
    @Id
    private UUID id;
    @ManyToOne
    @JsonIgnore
    private User user;
    @ManyToOne
    @JsonIgnore
    private Rooms rooms;
    private String commentary;
    private int rate;
    private LocalDate startTime;
    private LocalDate endTime;
    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY, mappedBy = "rends")
    private List<Payment> payment;
    @Override
    public String toString() {
        return "Rent{ " +
                "Room ID: " + rooms +
                ", Start Date: " + startTime +
                ", End Date: " + endTime +
                ", Commentary: '" + commentary + '\'' +
                ", Rate: " + rate +
                ", "+payment+
                " }";
    }


}
