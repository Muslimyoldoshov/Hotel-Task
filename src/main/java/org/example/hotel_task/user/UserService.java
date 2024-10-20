package org.example.hotel_task.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.example.hotel_task.exception.CustomErrorResponse;
import org.example.hotel_task.exception.EmailAlreadyExistException;
import org.example.hotel_task.exception.InvalidEmailAddressException;
import org.example.hotel_task.exception.PasswordNotMatchException;
import org.example.hotel_task.mail.EmailCodeService;
import org.example.hotel_task.mail.OTPRepository;
import org.example.hotel_task.mail.entity.OTP;
import org.example.hotel_task.pagelmpl.SpecificationBuilder;
import org.example.hotel_task.role.Type;
import org.example.hotel_task.user.dto.*;
import org.example.hotel_task.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final OTPRepository otpRepository;
    private final EmailCodeService emailCodeService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public List<UserResponseDto> getAllUsers(Pageable pageable, String predicate) {
        Specification<User> specification = SpecificationBuilder.build(predicate);
        if (specification == null) {
            return userRepository.findAll(pageable)
                    .map(user -> modelMapper.map(user, UserResponseDto.class))
                    .toList();
        }
        return userRepository.findAll(specification, pageable)
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
    }

    @Transactional
    public UserResponseDto signUp(UserCreateDto userCreateDto) {
        try {
            String password = userCreateDto.getPassword();
            String confirmPassword = userCreateDto.getConfirmPassword();
            String email = userCreateDto.getEmail();

            if (!emailCodeService.isValid(userCreateDto.getEmail())) {
                throw new InvalidEmailAddressException(String.format("%s is not valid", email));
            }
            if (otpRepository.findById(email).isPresent()) {
                throw new EmailAlreadyExistException(String.format("%s email is not verified. Please verify it!", email));
            }
            if (userRepository.findUserByEmail(email).isPresent()) {
                throw new EmailAlreadyExistException(String.format("User with email %s already exists", email));
            }
            if (!password.equals(confirmPassword)) {
                throw new PasswordNotMatchException("Password and confirm password do not match!");
            }

            userCreateDto.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));

            OTP otp = modelMapper.map(userCreateDto, OTP.class);
            otpRepository.save(otp);

            emailCodeService.sendEmail(email);

            return modelMapper.map(userCreateDto, UserResponseDto.class);
        } catch (InvalidEmailAddressException | EmailAlreadyExistException | PasswordNotMatchException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while signing up: " + e.getMessage(), e);
        }
    }

    public UserResponseDto signIn(UserSignInDto userSignInDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findUserByEmail(userSignInDto.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Email or password incorrect"));
        if (!passwordEncoder.matches(userSignInDto.getPassword(),user.getPassword())){
            throw new RuntimeException("Parolda moslik yoq");}
        otpRepository.deleteById(userSignInDto.getEmail());
        return modelMapper.map(user, UserResponseDto.class);

    }

    public UserResponseDto userSave(UserCreateDto userCreateDto) {
        try {
            User savedUser = userRepository.save(modelMapper.map(userCreateDto, User.class));
            return modelMapper.map(savedUser, UserResponseDto.class);
        } catch (Exception e) {
            throw new CustomErrorResponse("Error occurred while saving user: " + e.getMessage(), HttpStatus.NOT_FOUND,LocalDateTime.now());
        }
    }

    public UserResponseDto getByID(UUID id) {
        try {
            Optional<User> byId = userRepository.findById(id);
            return modelMapper.map(byId, UserResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching user by ID: " + e.getMessage(), e);
        }
    }

    public void deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while deleting user: " + e.getMessage(), e);
        }
    }

    public UserResponseDto updateUser(UUID id, UserUpdateDto userUpdateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            List<Type> userType = user.getUserType();
            modelMapper.map(userUpdateDto, user);

            if (userRepository.findUserByEmail(authentication.getName())
                    .get().getUserType().stream().anyMatch(type -> type.equals(Type.ADMIN))) {
                User savedUser = userRepository.save(user);
                return modelMapper.map(savedUser, UserResponseDto.class);
            }

            user.setSurname(userUpdateDto.getSurname());
            user.setName(userUpdateDto.getName());
            user.setUserType(userType);

            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDto.class);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while updating user: " + e.getMessage(), e);
        }
    }

    public UserUploadDto download() {
        try {
            List<UserResponseDto> users = userRepository.findAll().stream()
                    .map(user -> modelMapper.map(user, UserResponseDto.class))
                    .toList();
            String zipFilePath = "users-data.zip";

            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(fos)) {

                String docFileName = "users.docx";
                XWPFDocument document = new XWPFDocument();

                XWPFParagraph titleParagraph = document.createParagraph();
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("USERS Information");
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                for (UserResponseDto user : users) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.addBreak();
                    run.setText("USERS: " + user);
                }

                try (FileOutputStream docOuts = new FileOutputStream(docFileName)) {
                    document.write(docOuts);
                }

                File docFile = new File(docFileName);
                zipOut.putArchiveEntry(new ZipArchiveEntry(docFile.getName()));
                Files.copy(docFile.toPath(), zipOut);
                zipOut.closeArchiveEntry();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFilePath));
            return new UserUploadDto(resource, zipFilePath);

        } catch (IOException e) {
            throw new RuntimeException("Error occurred while downloading user data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }
}
