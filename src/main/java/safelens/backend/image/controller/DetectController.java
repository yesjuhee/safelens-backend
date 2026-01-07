package safelens.backend.image.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import safelens.backend.global.auth.AuthMember;
import safelens.backend.image.dto.DetectRequest;
import safelens.backend.image.dto.DetectResponse;
import safelens.backend.image.service.DetectService;
import safelens.backend.member.domain.Member;

@Slf4j
@RestController
@RequestMapping("/detect")
@RequiredArgsConstructor
public class DetectController {

    private final DetectService detectService;

    @PostMapping
    public ResponseEntity<DetectResponse> detectPersonalInfo(@Valid @RequestBody DetectRequest request,
                                                             @AuthMember Member member) {
        log.info("POST /detect 요청 - imageUuid: {}, memberId: {}", request.getImageUuid(), member.getId());

        DetectResponse response = detectService.detectPersonalInfo(request);
        return ResponseEntity.ok(response);
    }
}
