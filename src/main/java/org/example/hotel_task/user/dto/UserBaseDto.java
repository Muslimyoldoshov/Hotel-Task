package org.example.hotel_task.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseDto {


    @NotBlank
    private String name;
    private String surname;

    @Email
    @NotBlank(message = "auth.user.email.required")
    private String email;

}
