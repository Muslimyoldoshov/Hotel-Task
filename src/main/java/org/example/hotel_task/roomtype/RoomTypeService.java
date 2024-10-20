package org.example.hotel_task.roomtype;

import lombok.RequiredArgsConstructor;
import org.example.hotel_task.roomtype.entity.RoomType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepositary roomTypeRepositary;

    public RoomType createType(String roomtype) {
        RoomType roomType=new RoomType();
        roomType.setType(roomtype.toUpperCase());
        return roomTypeRepositary.save(roomType);
    }

    public List<RoomType> getAllType() {
        return roomTypeRepositary.findAll();
    }

    public RoomType byIdType(UUID id) {
        return roomTypeRepositary.findById(id).get();
    }

    public RoomType putType(UUID id, String roomType) {
        RoomType roomType1 = roomTypeRepositary.findById(id).get();
        roomType1.setType(roomType);
        return roomTypeRepositary.save(roomType1);
    }

    public void deleteType(UUID id) {
        roomTypeRepositary.deleteById(id);
    }
}
