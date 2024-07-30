package com.AI.chatbot.controller;

import com.AI.chatbot.model.CaseLowManagement;
import com.AI.chatbot.repository.CaseLowManagementRepository;
import com.AI.chatbot.util.S3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CaseLowManagementController {

    @Autowired
    private CaseLowManagementRepository repository;

    @Autowired
    private S3Utils s3Utils;

    @Autowired
    private AmazonS3 amazonS3;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file,
                                            @RequestParam("fileName") String fileName) {
        try {
            String fileUrl = s3Utils.uploadFile(file);
            CaseLowManagement caseLowManagement = new CaseLowManagement();
            caseLowManagement.setFileName(fileName);
            caseLowManagement.setFilePath(fileUrl);
            caseLowManagement.setUploadDate(LocalDate.now().toString());
            repository.save(caseLowManagement);

            return ResponseEntity.ok("파일 업로드 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("파일 업로드 실패");
        }
    }

    @GetMapping("/files")
    public List<CaseLowManagement> getAllFiles() {
        return repository.findAll();
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<String> getFile(@PathVariable("id") Long id) {
        CaseLowManagement fileRecord = repository.findById(id).orElse(null);

        if (fileRecord == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String fileContent = readFileFromS3(fileRecord.getFilePath());
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private String readFileFromS3(String fileUrl) throws IOException {
        String key = extractKeyFromUrl(fileUrl);
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(s3Utils.getBucket(), key));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl.startsWith("https://") && imageUrl.contains(".s3.amazonaws.com/")) {
            return imageUrl.split(".com/")[1];
        } else if (imageUrl.startsWith("https://s3.amazonaws.com/")) {
            return imageUrl.split("/", 4)[3];
        } else {
            throw new IllegalArgumentException("Invalid S3 URL format");
        }
    }
}
