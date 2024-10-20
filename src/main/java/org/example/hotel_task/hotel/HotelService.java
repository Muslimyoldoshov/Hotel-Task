package org.example.hotel_task.hotel;

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
import org.example.hotel_task.attachment.dto.AttachmentResponceDto;
import org.example.hotel_task.hotel.dto.HotelCreateDto;
import org.example.hotel_task.hotel.dto.HotelResponceDto;
import org.example.hotel_task.hotel.dto.HotelUpdateDto;
import org.example.hotel_task.hotel.dto.HotelUploadDto;
import org.example.hotel_task.hotel.entity.Hotel;
import org.example.hotel_task.pagelmpl.SpecificationBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepositary hotelRepositary;
    private final AttachmentRepositary attachmentRepositary;
    private final ModelMapper mapper = new ModelMapper();
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${service.upload.dir}")
    private String uploadDir;

    @Transactional
    public HotelResponceDto create(HotelCreateDto hotelCreateDto) {
        try {
            Hotel map = mapper.map(hotelCreateDto, Hotel.class);
            String filePath = Paths.get(uploadDir, hotelCreateDto.getMultipartFile().getOriginalFilename()).toString();

            File destFile = new File(filePath);
            hotelCreateDto.getMultipartFile().transferTo(destFile);

            Attachment attachment = new Attachment(UUID.randomUUID(), hotelCreateDto.getMultipartFile().getName(), filePath,
                    hotelCreateDto.getMultipartFile().getContentType(), LocalDateTime.now());
            entityManager.merge(attachment);

            Attachment savedAttachment = attachmentRepositary.save(attachment);
            map.setAttachment(savedAttachment);
            Hotel savedHotel = hotelRepositary.save(map);

            return mapper.map(savedHotel, HotelResponceDto.class);
        } catch (IOException e) {
            log.error("File upload failed: " + e.getMessage(), e);
            throw new RuntimeException("File upload failed", e);
        } catch (Exception e) {
            log.error("Error while creating hotel: " + e.getMessage(), e);
            throw new RuntimeException("Error while creating hotel", e);
        }
    }

    public List<HotelResponceDto> getAll(Pageable pageable, String predicate) {
        try {
            Specification<Hotel> specification = SpecificationBuilder.build(predicate);
            List<Hotel> hotels = (specification == null) ? hotelRepositary.findAll(pageable).getContent()
                    : hotelRepositary.findAll(specification, pageable).getContent();

            return hotels.stream().map(hotel -> mapper.map(hotel, HotelResponceDto.class)).toList();
        } catch (Exception e) {
            log.error("Error fetching hotels: " + e.getMessage(), e);
            throw new RuntimeException("Error fetching hotels", e);
        }
    }

    public HotelResponceDto getByIdHotel(UUID id) {
        try {
            Hotel hotel = hotelRepositary.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            HotelResponceDto responseDto = mapper.map(hotel, HotelResponceDto.class);
            responseDto.setAttachment(mapper.map(hotel.getAttachment(), AttachmentResponceDto.class));
            return responseDto;
        } catch (Exception e) {
            log.error("Error fetching hotel by ID: " + e.getMessage(), e);
            throw new RuntimeException("Error fetching hotel by ID", e);
        }
    }

    public void deleteHotel(UUID id) {
        try {
            hotelRepositary.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting hotel: " + e.getMessage(), e);
            throw new RuntimeException("Error deleting hotel", e);
        }
    }

    @Transactional
    public HotelResponceDto put(UUID id, HotelUpdateDto hotelUpdateDto) {
        try {
            Hotel hotel = hotelRepositary.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));

            Path path = Paths.get(hotel.getAttachment().getUrl());
            Files.deleteIfExists(path);

            String originalFileName = hotelUpdateDto.getMultipartFile().getOriginalFilename();
            String fileType = hotelUpdateDto.getMultipartFile().getContentType();
            String newFilePath = Paths.get(uploadDir, originalFileName).toString();

            File destFile = new File(newFilePath);
            hotelUpdateDto.getMultipartFile().transferTo(destFile);

            hotel.getAttachment().setUrl(newFilePath);
            hotel.getAttachment().setFileName(originalFileName);
            hotel.getAttachment().setFileType(fileType);
            hotel.getAttachment().setUploadTime(LocalDateTime.now());

            Attachment savedAttachment = attachmentRepositary.save(hotel.getAttachment());
            hotel.setAttachment(savedAttachment);

            mapper.map(hotelUpdateDto, hotel);

            Hotel updatedHotel = hotelRepositary.save(hotel);
            return mapper.map(updatedHotel, HotelResponceDto.class);
        } catch (IOException e) {
            log.error("Error updating hotel file: " + e.getMessage(), e);
            throw new RuntimeException("Error updating hotel file", e);
        } catch (Exception e) {
            log.error("Error updating hotel: " + e.getMessage(), e);
            throw new RuntimeException("Error updating hotel", e);
        }
    }

    public HotelUploadDto download() {
        try {
            List<HotelResponceDto> hotels = hotelRepositary.findAll().stream()
                    .map(hotel -> mapper.map(hotel, HotelResponceDto.class)).toList();

            String zipFilePath = "hotel-data.zip";
            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(fos)) {

                String docFileName = "hotel.docx";
                XWPFDocument document = new XWPFDocument();

                XWPFParagraph titleParagraph = document.createParagraph();
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("HOTEL Information");
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                for (HotelResponceDto hotel : hotels) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.addBreak();
                    run.setText("HOTELS:" + hotel);
                    try (FileOutputStream docOuts = new FileOutputStream(docFileName)) {
                        document.write(docOuts);
                    }

                    File docFile = new File(docFileName);
                    zipOut.putArchiveEntry(new ZipArchiveEntry(docFile.getName()));
                    Files.copy(docFile.toPath(), zipOut);
                    zipOut.closeArchiveEntry();
                }

                InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFilePath));
                return new HotelUploadDto(resource, zipFilePath);
            }
        } catch (IOException e) {
            log.error("Error downloading hotel data: " + e.getMessage(), e);
            throw new RuntimeException("Error downloading hotel data", e);
        } catch (Exception e) {
            log.error("Error processing download: " + e.getMessage(), e);
            throw new RuntimeException("Error processing download", e);
        }
    }
}
