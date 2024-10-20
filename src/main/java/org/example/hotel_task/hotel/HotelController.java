package org.example.hotel_task.hotel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotel_task.hotel.dto.HotelCreateDto;
import org.example.hotel_task.hotel.dto.HotelResponceDto;
import org.example.hotel_task.hotel.dto.HotelUpdateDto;
import org.example.hotel_task.hotel.dto.HotelUploadDto;
import org.example.hotel_task.user.dto.UserUploadDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponceDto> create(@ModelAttribute HotelCreateDto hotelCreateDto) throws IOException {
        MultipartFile file = hotelCreateDto.getMultipartFile();

        return switch (Objects.requireNonNull(file.getContentType())) {
            case MediaType.IMAGE_GIF_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_PNG_VALUE -> {
                HotelResponceDto hotelResponceDto = hotelService.create(hotelCreateDto);
                yield ResponseEntity.ok(hotelResponceDto);
            }
            default -> {
                log.error("Unsupported filetype: {}", file.getContentType());
                throw new UnsupportedMediaTypeStatusException(
                        String.format("Unsupported filetype: %s", file.getContentType()));
            }
        };
    }

    @GetMapping
    public ResponseEntity<List<HotelResponceDto>>getAll(Pageable pageable, @RequestParam(required = false) String predicate){
       List<HotelResponceDto>hotelResponceDtos= hotelService.getAll(pageable,predicate);
       return ResponseEntity.status(HttpStatus.OK).body(hotelResponceDtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponceDto>getById(@PathVariable UUID id){
        HotelResponceDto hotelResponceDto=hotelService.getByIdHotel(id);
        return ResponseEntity.ok(hotelResponceDto);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?>delete(@PathVariable UUID id){
        hotelService.deleteHotel(id);
        return ResponseEntity.ok().body("");
    }
    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<HotelResponceDto>update(@PathVariable UUID id,@ModelAttribute HotelUpdateDto hotelUpdateDto) throws IOException {
        HotelResponceDto hotelResponceDto=hotelService.put(id,hotelUpdateDto);
        return ResponseEntity.ok(hotelResponceDto);
    }
    @GetMapping("/download")
   @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        HotelUploadDto upload = hotelService.download();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + upload.getZipFilePath())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(upload.getResource());}
}
