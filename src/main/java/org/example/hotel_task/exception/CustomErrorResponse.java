package org.example.hotel_task.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomErrorResponse extends RuntimeException{
    private String message;
    private HttpStatus status;
    private Map<String, Object> errors;
    private LocalDateTime timestamp;

    public CustomErrorResponse(String message, HttpStatus status, LocalDateTime timestamp) {
        super();
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}
