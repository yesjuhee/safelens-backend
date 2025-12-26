package safelens.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import safelens.backend.dto.ImageUploadResponse;
import safelens.backend.service.ImageUploadService;

@Slf4j
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("POST /images/upload 요청 - 파일명: {}", file.getOriginalFilename());

        try {
            ImageUploadResponse response = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("이미지 업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
