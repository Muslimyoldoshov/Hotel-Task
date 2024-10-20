package org.example.hotel_task.room;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.example.hotel_task.attachment.Attachment;
import org.example.hotel_task.attachment.dto.AttachmentRepositary;
import org.example.hotel_task.hotel.HotelRepositary;
import org.example.hotel_task.hotel.entity.Hotel;
import org.example.hotel_task.pagelmpl.SpecificationBuilder;
import org.example.hotel_task.rend.RendRepository;
import org.example.hotel_task.rend.entity.Rend;
import org.example.hotel_task.room.dto.RoomCreateDto;
import org.example.hotel_task.room.dto.RoomResponcedto;
import org.example.hotel_task.room.dto.RoomUpdateDto;
import org.example.hotel_task.room.dto.RoomUpload;
import org.example.hotel_task.room.entity.Rooms;
import org.example.hotel_task.roomtype.entity.RoomType;
import org.example.hotel_task.roomtype.RoomTypeRepositary;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomsRepository roomsRepository;
    private final HotelRepositary hotelRepositary;
    private final RoomTypeRepositary roomTypeRepositary;
    private final AttachmentRepositary attachmentRepositary;
    private final RendRepository rendRepository;
    private final ModelMapper mapper = new ModelMapper();

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${service.upload.dir}")
    private String uploadDir;

    public List<RoomResponcedto> getAll(Pageable pageable, String predicate) {
        try {
            Specification<Rooms> specification = SpecificationBuilder.build(predicate);
            if (specification == null) {
                return roomsRepository.findAll(pageable)
                        .map(rooms -> mapper.map(rooms, RoomResponcedto.class))
                        .toList();
            }
            return roomsRepository.findAll(specification, pageable)
                    .map(rooms -> mapper.map(rooms, RoomResponcedto.class))
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching rooms with predicate: {}", predicate, e);
            throw new RuntimeException("Error fetching rooms data", e);
        }
    }

    @Transactional
    public RoomResponcedto createRooms(RoomCreateDto roomCreateDto) {
        try {
            Rooms map = mapper.map(roomCreateDto, Rooms.class);
            Hotel hotel = hotelRepositary.findById(roomCreateDto.getHotelId())
                    .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + roomCreateDto.getHotelId()));

            String filePath = Paths.get(uploadDir, roomCreateDto.getMultipartFile().getOriginalFilename()).toString();
            File destFile = new File(filePath);
            roomCreateDto.getMultipartFile().transferTo(destFile);

            Attachment attachment = new Attachment(
                    UUID.randomUUID(), roomCreateDto.getMultipartFile().getOriginalFilename(),
                    filePath, roomCreateDto.getMultipartFile().getContentType(),
                    LocalDateTime.now());

            Attachment savedAttachment = attachmentRepositary.save(attachment);

            RoomType roomType = roomTypeRepositary.findById(roomCreateDto.getRoomTypeId())
                    .orElseThrow(() -> new RuntimeException("RoomType not found with id: " + roomCreateDto.getRoomTypeId()));

            map.setRoomType(roomType);
            map.setHotels(hotel);
            map.setAttachment(savedAttachment);

            Rooms savedRoom = roomsRepository.save(map);

            return mapper.map(savedRoom, RoomResponcedto.class);
        } catch (IOException e) {
            log.error("Error while creating room", e);
            throw new RuntimeException("Error while creating room", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating room", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

    public RoomResponcedto getByID(UUID id) {
        try {
            Rooms rooms = roomsRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
            return mapper.map(rooms, RoomResponcedto.class);
        } catch (Exception e) {
            log.error("Error fetching room by ID: {}", id, e);
            throw new RuntimeException("Room not found", e);
        }
    }

    @Transactional
    public RoomResponcedto put(UUID id, RoomUpdateDto updateDto) {
        try {
            Rooms rooms = roomsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            Path paths = Paths.get(rooms.getAttachment().getUrl());
            Files.deleteIfExists(paths);

            String fileName = updateDto.getMultipartFile().getOriginalFilename();
            String fileTypes = updateDto.getMultipartFile().getContentType();
            String newFilePath = String.valueOf(Paths.get(uploadDir, fileName));

            File destFile = Paths.get(uploadDir, fileName).toFile();
            updateDto.getMultipartFile().transferTo(destFile);

            Hotel hotel = hotelRepositary.findById(updateDto.getHotelId()).get();
            RoomType roomType = roomTypeRepositary.findById(updateDto.getRoomTypeId()).get();
            rooms.setRoomType(roomType);
            rooms.setHotels(hotel);
            rooms.setNumber(updateDto.getNumber());
            rooms.setPrice(updateDto.getPrice());
            rooms.setCapacity(updateDto.getCapacity());

            rooms.getAttachment().setUrl(newFilePath);
            rooms.getAttachment().setFileName(fileName);
            rooms.getAttachment().setFileType(fileTypes);
            rooms.getAttachment().setUploadTime(LocalDateTime.now());

            Attachment savedAttachment = attachmentRepositary.save(rooms.getAttachment());
            rooms.setAttachment(savedAttachment);

            Rooms updatedRooms = roomsRepository.save(rooms);

            return mapper.map(updatedRooms, RoomResponcedto.class);
        } catch (IOException e) {
            log.error("Error updating room with ID: {}", id, e);
            throw new RuntimeException("Error updating room", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating room", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

    public void deleteById(UUID id) {
        try {
            roomsRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting room with ID: {}", id, e);
            throw new RuntimeException("Error deleting room", e);
        }
    }

    public List<RoomResponcedto> getAllRooms() {
        try {
            return roomsRepository.findAll()
                    .stream()
                    .map(rooms -> mapper.map(rooms, RoomResponcedto.class))
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching all rooms", e);
            throw new RuntimeException("Error fetching rooms data", e);
        }
    }

    public RoomUpload downloadRoomsRentAndPaymentsAsDoc() {
        try {
            List<RoomResponcedto> rooms = getAllRooms();
            String zipFilePath = "rooms-data.zip";

            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(fos)) {

                String docFileName = "rooms-rent-payments-data.docx";
                Path tempDocPath = Paths.get(docFileName);
                XWPFDocument document = new XWPFDocument();

                XWPFParagraph titleParagraph = document.createParagraph();
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("Room, Rent and Payment Information");
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                for (RoomResponcedto room : rooms) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.addBreak();
                    run.setText("ROOM [");
                    run.addBreak();
                    run.setText("Room Price: " + room.getPrice());
                    run.addBreak();
                    run.setText("Room Number: " + room.getNumber());
                    run.addBreak();
                    run.setText("Room Capacity: " + room.getCapacity());
                    run.addBreak();
                    run.setText("Room Type: " + room.getRoomType().getType());
                    run.addBreak();

                    List<Rend> byRoomsId = rendRepository.findByRoomsId(room.getId());
                    for (Rend rent : byRoomsId) {
                        run.addBreak();
                        run.setText("RENT: " + rent);
                        run.addBreak();
                        run.setText("Payment: " + rent.getPayment());
                    }

                    run.addBreak();
                    run.setText("];!!!!!!!!!!!!!!!!!!!!");
                    run.addBreak();
                }

                try (FileOutputStream docOut = new FileOutputStream(tempDocPath.toFile())) {
                    document.write(docOut);
                }

                File docFile = tempDocPath.toFile();
                zipOut.putArchiveEntry(new ZipArchiveEntry(docFile.getName()));
                Files.copy(docFile.toPath(), zipOut);
                zipOut.closeArchiveEntry();

                document.close();
                Files.delete(tempDocPath);
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFilePath));
            return new RoomUpload(resource, zipFilePath);

        } catch (IOException e) {
            log.error("Error while downloading rooms rent and payments data as DOC", e);
            throw new RuntimeException("Error while downloading data", e);
        } catch (Exception e) {
            log.error("Unexpected error while downloading rooms rent and payments data as DOC", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

}
