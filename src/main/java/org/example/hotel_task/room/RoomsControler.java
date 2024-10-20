package org.example.hotel_task.room;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.example.hotel_task.room.dto.RoomCreateDto;
import org.example.hotel_task.room.dto.RoomResponcedto;
import org.example.hotel_task.room.dto.RoomUpdateDto;
import org.example.hotel_task.room.dto.RoomUpload;
import org.example.hotel_task.user.dto.UserResponseDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomsControler {
    private final RoomService roomService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomResponcedto>>getAll(Pageable pageable,@RequestParam(required = false) String predicate){
        List<RoomResponcedto> roomsList=roomService.getAll(pageable,predicate);
        return ResponseEntity.ok(roomsList);
    }
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponcedto>create(@ModelAttribute RoomCreateDto roomCreateDto) throws IOException {
        RoomResponcedto rooms = roomService.createRooms(roomCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rooms);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponcedto>getById(@PathVariable UUID id){
        RoomResponcedto roomResponcedto=roomService.getByID(id);
        return ResponseEntity.ok(roomResponcedto);
    }
    @PutMapping(value = "/{id}" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RoomResponcedto>update(@PathVariable UUID id, @ModelAttribute RoomUpdateDto roomUpdateDto) throws IOException {
        RoomResponcedto put = roomService.put(id, roomUpdateDto);
        return ResponseEntity.ok(put);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")

    public ResponseEntity<?>delete(@PathVariable UUID id){
        roomService.deleteById(id);
        return ResponseEntity.ok("");
    }
    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> downloadRoomData() throws IOException {
        RoomUpload upload = roomService.downloadRoomsRentAndPaymentsAsDoc();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + upload.getZipFilePath())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(upload.getResource());}
}
