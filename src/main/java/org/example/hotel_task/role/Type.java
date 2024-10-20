package org.example.hotel_task.role;

import lombok.Getter;
import org.example.hotel_task.permission.Permision;

import java.util.Set;

@Getter
public enum Type {

    USER(Set.of()),
    ADMIN(Set.of(Permision.GET_ALL, Permision.CREATE, Permision.DELETE, Permision.UPDATE));

    private final Set<Permision> permissions;

    Type(Set<Permision> permissions) {
        this.permissions = permissions;
    }
}
