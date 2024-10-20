package org.example.hotel_task.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hotel_task.room.dto.RoomUpload;
import org.example.hotel_task.user.dto.UserResponseDto;
import org.example.hotel_task.user.dto.UserUpdateDto;
import org.example.hotel_task.user.dto.UserUploadDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>>getAll(Pageable pageable,@RequestParam(required = false) String predicate){
        List<UserResponseDto> allUsers = userService.getAllUsers(pageable,predicate);
        return ResponseEntity.ok(allUsers);
    }
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto>getById(@PathVariable UUID id){
        UserResponseDto byID = userService.getByID(id);
        return ResponseEntity.ok(byID);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?>delete(@PathVariable UUID id)
    {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto>put(@PathVariable UUID id,  @Valid @RequestBody UserUpdateDto userUpdateDto)
    {
        System.out.println("userUpdateDto = " + userUpdateDto.getName());
        UserResponseDto userResponseDto=userService.updateUser(id,userUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }
    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        UserUploadDto upload = userService.download();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + upload.getZipFilePath())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(upload.getResource());}
}
