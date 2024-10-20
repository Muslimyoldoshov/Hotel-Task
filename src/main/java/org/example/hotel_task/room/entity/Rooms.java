package org.example.hotel_task.room.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.hotel_task.attachment.Attachment;
import org.example.hotel_task.hotel.entity.Hotel;
import org.example.hotel_task.roomtype.entity.RoomType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

public class Rooms {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String number;
    private int capacity;
    private Double price;
     @ManyToOne
     @JoinColumn(name = "room_type_id")
    private RoomType roomType;
    @ManyToOne
    @JsonIgnore
    private Hotel hotels;
    @OneToOne(cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;
    @Override
    public String toString() {
        return "Rooms{" +
                "id=" + id +
                ", name='" + number + '\'' +
                ", capacity=" + capacity +
                ", price=" + price +
                '}';
    }
}
