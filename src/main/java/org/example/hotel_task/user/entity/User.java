package org.example.hotel_task.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.hotel_task.attachment.Attachment;
import org.example.hotel_task.card.entity.Card;
import org.example.hotel_task.permission.Permision;
import org.example.hotel_task.role.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Type> userType;
   @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Card> cards;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Type role : userType) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
            for (Permision permision : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permision.toString()));
            }
        }
        return authorities;
    }
    @Override
    public String toString() {
        return "User{" +
                ", Name: " + name +
                ", Surname: " + surname +
                ", Email: " + email +
                ", User Types: " + userType +
                "}";
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
