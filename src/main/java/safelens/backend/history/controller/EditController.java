package safelens.backend.history.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import safelens.backend.global.auth.AuthMember;
import safelens.backend.history.dto.EditRequest;
import safelens.backend.history.dto.EditResponse;
import safelens.backend.history.service.EditService;
import safelens.backend.member.domain.Member;

@Slf4j
@RestController
@RequestMapping("/edit")
@RequiredArgsConstructor
public class EditController {

    private final EditService editService;

    @PostMapping
    public ResponseEntity<EditResponse> editImage(@Valid @RequestBody EditRequest request,
                                                  @AuthMember Member member) {
        log.info("POST /edit 요청 - imageUuid: {}, memberId: {}", request.getImageUuid(), member.getId());

        EditResponse response = editService.editImage(request, member);
        return ResponseEntity.ok(response);
    }
}
