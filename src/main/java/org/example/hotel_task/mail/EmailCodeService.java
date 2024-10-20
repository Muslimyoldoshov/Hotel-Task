package org.example.hotel_task.mail;

import com.sun.jdi.InvalidCodeIndexException;
import io.netty.handler.codec.CodecException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Code;
import org.example.hotel_task.exception.EmailVerificationException;
import org.example.hotel_task.exception.PasswordNotMatchException;
import org.example.hotel_task.mail.dto.OtpVerifyDto;
import org.example.hotel_task.mail.entity.EmailCode;
import org.example.hotel_task.mail.entity.OTP;
import org.example.hotel_task.role.Type;
import org.example.hotel_task.user.UserRepository;
import org.example.hotel_task.user.dto.UserForgotPasswordDto;
import org.example.hotel_task.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.directory.InvalidAttributesException;
import java.rmi.NotBoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class EmailCodeService {

    private final EmailCodeRepository emailCodeRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final OTPRepository otpRepository;
    private final ModelMapper mapper=new ModelMapper();
    private final String text = "Hi, someone tried to sign up for an Hotel App account with %s . " +
            "If it was you, enter this confirmation code in the app:" + System.lineSeparator()+
            " %d";
    @Transactional
    public void sendEmail(String email) {

        Optional<EmailCode> optionalEmailCode = emailCodeRepository.findById( email );
        System.out.println("optionalEmailCode = " + optionalEmailCode);
        if( optionalEmailCode.isEmpty() ) {
            int code = generateCode();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Hotel task APP");
            message.setText(text.formatted(email,code));
            mailSender.send(message);

            EmailCode emailCode = new EmailCode( email, String.valueOf( code ), LocalDateTime.now(), 1 );
            emailCodeRepository.save(emailCode);
            System.out.println(emailCode);
            System.out.println( "Email code = "+code );
        }
        else
        {
            EmailCode emailCode = optionalEmailCode.get();

            if( emailCode.getSentCount() >= 5 )
            {
                throw new EmailVerificationException( "You already tried 5 times. Please try after 24 hour" );
            }

            if( !emailCode.getLastSentTime().plusMinutes(2).isBefore( LocalDateTime.now() ) )
            {
                Duration between = Duration.between( emailCode.getLastSentTime(), LocalDateTime.now() );
                long diff = 30 - between.getSeconds();
                throw new EmailVerificationException( "Please try after %d seconds".formatted( diff ) );
            }

            int code = generateCode();

            emailCode.setCode( String.valueOf( code ) );
            emailCode.setSentCount( emailCode.getSentCount() + 1 );
            emailCode.setLastSentTime( LocalDateTime.now() );

            emailCodeRepository.save( emailCode );

            System.out.println( "Email code = "+code );
        }
    }

    public void verifyCode( OtpVerifyDto verifyDto )
    {
        String email = verifyDto.getEmail();
        String code = String.valueOf(verifyDto.getCode());

        EmailCode emailCode = emailCodeRepository.findById( email )
                                    .orElseThrow( () -> new EmailVerificationException( "the email not registered with Hotel  App+" ) );

        if( emailCode.getLastSentTime().plusMinutes( 5 ).isBefore( LocalDateTime.now() ) ) {
            throw new EmailVerificationException( "the email code already expired" );
        }

        if( !emailCode.getCode().equals( code ) ) {
            throw new EmailVerificationException( "Email code doesn't match" );
        }


        OTP otpUser = otpRepository.findById( email )
                                    .orElseThrow(
                                      () -> new EntityNotFoundException(
                                          String.format( "User with email = %s not found", email)
                                      ) );


        User save = mapper.map(otpUser, User.class);
        save.setUserType(Collections.singletonList(Type.USER));
        userRepository.save(save);
        otpRepository.delete(otpUser);
        otpRepository.deleteAll();
    }




    public boolean isValid(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
    private int generateCode(){
        Random random = new Random();
        return random.nextInt( 100000, 999999 );
    }


    public void forgotPassword(String email, UserForgotPasswordDto forgotPasswordDto) throws InvalidAttributesException {
        EmailCode emailCode = emailCodeRepository.findById(email).get();
        if (emailCode.getCode().equals(forgotPasswordDto.getCode())){
            if (forgotPasswordDto.getPassword().equals(forgotPasswordDto.getConfirmPassword())){
                User user = userRepository.findUserByEmail(email).get();
                user.setPassword(forgotPasswordDto.getPassword());
                userRepository.save(user);
            }

        }

    }
}
