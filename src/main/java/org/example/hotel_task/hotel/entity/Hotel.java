package org.example.hotel_task.hotel.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hotel_task.attachment.Attachment;
import org.example.hotel_task.room.entity.Rooms;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    private String name;
    private String location;
    private int stars;
    @OneToMany(mappedBy = "hotels",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<Rooms>rooms;
    @Override
    public String toString() {
        return "Hotel{" +
                "UUID: " + uuid +
                ", Name: '" + name + '\'' +
                ", Location: '" + location + '\'' +
                ", Stars: " + stars +
                ", Room Count: " + (rooms != null ? rooms.size() : 0) +
                '}';
    }

    @OneToOne(cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "attachment_id")
   private Attachment attachment;
}
