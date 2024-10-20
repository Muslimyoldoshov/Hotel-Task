package org.example.hotel_task.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.example.hotel_task.role.Type;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotEmpty
    private List<Type> userTypes;

}