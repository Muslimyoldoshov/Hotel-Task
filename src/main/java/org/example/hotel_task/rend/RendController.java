package org.example.hotel_task.rend;

import lombok.RequiredArgsConstructor;
import org.example.hotel_task.exception.CustomErrorResponse;
import org.example.hotel_task.rend.dto.RendResponseDto;
import org.example.hotel_task.rend.dto.RentCreateDto;
import org.example.hotel_task.rend.dto.RentUploadDto;
import org.example.hotel_task.room.dto.RoomUpload;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/rent")
@RequiredArgsConstructor
public class RendController {
    private final RendService rendService;
    @PostMapping
    public ResponseEntity<RendResponseDto>create(@RequestBody RentCreateDto rentCreateDto ,
                                                 @RequestParam("startDate") LocalDate startTime,
                                                 @RequestParam("endDate") LocalDate endTime) throws RendService.CustomErrorResponse {
        RendResponseDto rent = rendService.createRent(rentCreateDto,startTime,endTime);
        return ResponseEntity.status(HttpStatus.OK).body(rent);
    }

}
