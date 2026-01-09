package safelens.backend.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import safelens.backend.global.auth.AuthMember;
import safelens.backend.member.domain.Member;
import safelens.backend.member.dto.UserResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthMember Member member) {
        log.info("GET /users/me 요청 by memberId {}", member.getId());

        UserResponse response = new UserResponse(member);
        return ResponseEntity.ok(response);
    }
}
