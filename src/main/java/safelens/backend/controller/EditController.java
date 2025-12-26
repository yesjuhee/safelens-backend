package safelens.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import safelens.backend.dto.EditRequest;
import safelens.backend.dto.EditResponse;
import safelens.backend.service.EditService;

@Slf4j
@RestController
@RequestMapping("/edit")
@RequiredArgsConstructor
public class EditController {

    private final EditService editService;

    @PostMapping
    public ResponseEntity<EditResponse> editImage(@Valid @RequestBody EditRequest request) {
        log.info("POST /edit 요청 - imageUuid: {}, memberId: {}", request.getImageUuid(), request.getMemberId());

        try {
            EditResponse response = editService.editImage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("이미지 편집 요청 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
