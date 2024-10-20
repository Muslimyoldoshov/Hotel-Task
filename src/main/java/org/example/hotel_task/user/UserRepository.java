package org.example.hotel_task.user;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hotel_task.card.entity.Card;
import org.example.hotel_task.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findUserByEmail(String email);
    User findByName(String name);
    @Query("SELECT c FROM cards c WHERE c.id = :cartId")
    Card findByCards(@Param("cartId") UUID cartId );

}
