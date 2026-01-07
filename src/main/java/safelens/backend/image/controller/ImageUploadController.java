package safelens.backend.image.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import safelens.backend.global.auth.AuthMember;
import safelens.backend.image.dto.ImageUploadResponse;
import safelens.backend.image.service.ImageUploadService;
import safelens.backend.member.domain.Member;

@Slf4j
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file,
                                                           @AuthMember Member member) {
        log.info("POST /images/upload 요청 - 파일명: {}, memberId: {}", file.getOriginalFilename(), member.getId());

        ImageUploadResponse response = imageUploadService.uploadImage(file);
        return ResponseEntity.ok(response);
    }
}
