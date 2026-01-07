package safelens.backend.image.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import safelens.backend.image.dto.DetectRequest;
import safelens.backend.image.dto.DetectResponse;
import safelens.backend.image.service.DetectService;

@Slf4j
@RestController
@RequestMapping("/detect")
@RequiredArgsConstructor
public class DetectController {

    private final DetectService detectService;

    @PostMapping
    public ResponseEntity<DetectResponse> detectPersonalInfo(@Valid @RequestBody DetectRequest request) {
        log.info("POST /detect 요청 - imageUuid: {}", request.getImageUuid());

        try {
            DetectResponse response = detectService.detectPersonalInfo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("감지 요청 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
