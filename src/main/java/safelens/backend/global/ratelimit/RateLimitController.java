package safelens.backend.global.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitService rateLimitService;

    /**
     * 현재 Rate Limit 상태 조회
     * GET /limit
     */
    @GetMapping("/limit")
    public ResponseEntity<RateLimitStatusResponse> getStatus() {
        log.info("GET /limit 요청");

        RateLimitStatusResponse status = rateLimitService.getStatus();

        log.info("Rate Limit 상태 - 시간당: {}/{}, 일당: {}/{}",
                status.getHourlyUsed(), status.getHourlyLimit(),
                status.getDailyUsed(), status.getDailyLimit());

        return ResponseEntity.ok(status);
    }
}
