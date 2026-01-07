package safelens.backend.member.service;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import safelens.backend.global.util.JwtUtil;
import safelens.backend.global.util.PasswordUtil;
import safelens.backend.member.domain.Member;
import safelens.backend.member.dto.AuthResponse;
import safelens.backend.member.dto.LoginRequest;
import safelens.backend.member.dto.SignupRequest;
import safelens.backend.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // 아이디 중복 체크
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다");
        }

        // 비밀번호 암호화
        String encodedPassword = PasswordUtil.encode(request.getPassword());

        // Member 생성 및 저장
        Member member = new Member(
                null,
                request.getNickname(),
                request.getUsername(),
                encodedPassword,
                null,
                null,
                new ArrayList<>()
        );
        Member savedMember = memberRepository.save(member);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(savedMember.getUsername());

        return new AuthResponse(token, savedMember.getUsername(), savedMember.getNickname());
    }

    /**
     * 로그인
     */
    public AuthResponse login(LoginRequest request) {
        // 아이디로 회원 조회
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!PasswordUtil.matches(request.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다");
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(member.getUsername());

        return new AuthResponse(token, member.getUsername(), member.getNickname());
    }
}
