package org.example.hotel_task.roomtype;

import lombok.RequiredArgsConstructor;
import org.example.hotel_task.roomtype.entity.RoomType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roomtype")
@RequiredArgsConstructor
public class RoomTypeController {
    private final RoomTypeService roomTypeService;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomType>create(@RequestParam String roomtype){
        RoomType type = roomTypeService.createType(roomtype);
        return ResponseEntity.status(HttpStatus.CREATED).body(type);
    }
    @GetMapping
    @PreAuthorize("#hasRole('ADMIN')")
    public ResponseEntity<List<RoomType>>getAll(){
        List<RoomType> allType = roomTypeService.getAllType();
        return ResponseEntity.ok(allType);
    }
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RoomType>getById(@PathVariable UUID id){
        RoomType roomType = roomTypeService.byIdType(id);
        return ResponseEntity.status(HttpStatus.OK).body(roomType);
    }
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RoomType>put(@PathVariable UUID id,@RequestParam String roomType){
        RoomType roomType1 = roomTypeService.putType(id, roomType);
        return ResponseEntity.ok().body(roomType1);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?>delete(@PathVariable UUID id){
        roomTypeService.deleteType(id);
        return ResponseEntity.ok().body("");
    }
}
