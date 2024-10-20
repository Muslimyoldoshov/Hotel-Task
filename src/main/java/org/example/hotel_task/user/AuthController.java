package org.example.hotel_task.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.example.hotel_task.mail.EmailCodeService;
import org.example.hotel_task.mail.dto.OtpVerifyDto;
import org.example.hotel_task.jwt.JwtService;
import org.example.hotel_task.user.dto.UserCreateDto;
import org.example.hotel_task.user.dto.UserForgotPasswordDto;
import org.example.hotel_task.user.dto.UserResponseDto;
import org.example.hotel_task.user.dto.UserSignInDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.InvalidAttributesException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailCodeService emailService;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody @Valid UserCreateDto userCreateDto ) {
        UserResponseDto userResponseDto = userService.signUp(userCreateDto);
        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body( userResponseDto );
    }
    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> signIn(@RequestBody @Valid UserSignInDto userSignInDto){
        UserResponseDto userResponseDto = userService.signIn(userSignInDto);

        String token = jwtService.generateToken(userResponseDto.getEmail());
        return ResponseEntity
                .status( HttpStatus.OK )
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token))
                .body( userResponseDto );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verify(@RequestBody @Valid OtpVerifyDto verifyDto){
        emailService.verifyCode(verifyDto);
        return ResponseEntity
                .status( HttpStatus.OK )
                .body( "successfully verified" );
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestParam @Email String email){
        emailService.sendEmail(email);
        return ResponseEntity
                .status( HttpStatus.OK )
                .body( "Successfully send. Please, check your email!" );
    }
      @PostMapping("/forgot-password")
    public ResponseEntity<?>forgot_Password(@RequestBody @Email String email, @RequestBody UserForgotPasswordDto userForgotPasswordDto) throws InvalidAttributesException {
          emailService.forgotPassword(email,userForgotPasswordDto);
          return ResponseEntity.ok("Password change success!!!");
      }



}
