package safelens.backend.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import safelens.backend.global.ratelimit.RateLimitProperties;
import safelens.backend.global.ratelimit.RateLimitService;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final RateLimitProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        // 1. API 종류에 따른 포인트 계산
        int cost = calculateCost(requestURI);

        // upload는 카운팅하지 않음 (cost = 0)
        if (cost == 0) {
            log.debug("Rate limit 제외 - URI: {}", requestURI);
            return true;
        }

        // 2. Rate Limit 체크 (with Redis 장애 fallback)
        boolean allowed;
        try {
            allowed = rateLimitService.checkAndIncrement(cost);
        } catch (Exception e) {
            log.error("Rate limit 체크 실패 - Redis 장애 가능성, 요청 허용 처리", e);
            return true;  // Redis 장애 시 서비스 중단을 피하기 위해 통과
        }

        // 3. 리미트 초과 시 429 응답
        if (!allowed) {
            handleRateLimitExceeded(response);
            return false;
        }

        log.debug("Rate limit 통과 - cost: {}, URI: {}", cost, requestURI);
        return true;
    }

    /**
     * URL 패턴에 따라 API 비용 계산
     */
    private int calculateCost(String requestURI) {
        if (requestURI.matches(".*/images/upload")) {
            return properties.getApiCosts().getUpload();  // 0
        } else if (requestURI.matches(".*/detect")) {
            return properties.getApiCosts().getDetect();  // 1
        } else if (requestURI.matches(".*/edit")) {
            return properties.getApiCosts().getEdit();    // 20
        }
        return 0;  // 기타 API는 카운팅 안함
    }

    /**
     * 429 Too Many Requests 응답 처리
     */
    private void handleRateLimitExceeded(HttpServletResponse response) throws Exception {
        log.warn("Rate limit 초과 - 429 응답 반환");

        response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
        response.setContentType("application/json;charset=UTF-8");

        // Retry-After 헤더 추가 (다음 정시까지 초 단위)
        long retryAfterSeconds = rateLimitService.getSecondsUntilReset();
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

        // 에러 응답 바디
        String errorBody = String.format(
                "{\"status\":429,\"error\":\"Too Many Requests\"," +
                        "\"message\":\"API 호출 한도를 초과했습니다. %d초 후 다시 시도해주세요.\"," +
                        "\"retryAfter\":%d}",
                retryAfterSeconds, retryAfterSeconds
        );
        response.getWriter().write(errorBody);
    }
}
