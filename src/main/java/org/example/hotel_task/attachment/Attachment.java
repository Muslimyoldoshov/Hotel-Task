package org.example.hotel_task.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.hotel_task.hotel.entity.Hotel;
import org.example.hotel_task.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(EntityListeners.class)
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID attachment_id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String url;

    private String fileType;

    @CreationTimestamp
    private LocalDateTime uploadTime;


}


