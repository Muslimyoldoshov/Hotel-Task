package org.example.hotel_task.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserForgotPasswordDto {
   private String code;
     private String password;
    private String confirmPassword;

}
