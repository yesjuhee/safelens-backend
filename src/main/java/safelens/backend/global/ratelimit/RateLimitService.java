package safelens.backend.global.ratelimit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String HOURLY_KEY_FORMAT = "rate_limit:hourly:%s";
    private static final String DAILY_KEY_FORMAT = "rate_limit:daily:%s";
    private static final DateTimeFormatter HOURLY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HH");
    private static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;

    /**
     * Lua Script: 원자적으로 체크 + 증가
     * - 현재 카운터 값 조회
     * - 추가 후 리미트 초과 여부 확인
     * - 리미트 이하면 증가 후 TTL 설정, 초과면 실패
     */
    private static final String LUA_SCRIPT =
            "local current = redis.call('GET', KEYS[1]) " +
            "current = tonumber(current) or 0 " +
            "if current + tonumber(ARGV[1]) <= tonumber(ARGV[2]) then " +
            "  redis.call('INCRBY', KEYS[1], ARGV[1]) " +
            "  redis.call('EXPIRE', KEYS[1], ARGV[3]) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";

    /**
     * Rate Limit 체크 및 증가 (전역 카운터)
     *
     * @param cost API 호출 포인트 (detect=1, edit=20)
     * @return true: 허용, false: 리미트 초과
     */
    public boolean checkAndIncrement(int cost) {
        LocalDateTime now = LocalDateTime.now(ZONE_SEOUL);

        // 1. 시간당 체크
        String hourlyKey = String.format(HOURLY_KEY_FORMAT, now.format(HOURLY_FORMATTER));
        long hourlyTtl = getSecondsUntilNextHour(now) + 3600; // 다음 정시 + 1시간 버퍼
        if (!checkAndIncrementAtomic(hourlyKey, cost, properties.getHourlyLimit(), hourlyTtl)) {
            log.warn("시간당 리미트 초과 - hourlyKey: {}", hourlyKey);
            return false;
        }

        // 2. 일당 체크
        String dailyKey = String.format(DAILY_KEY_FORMAT, now.format(DAILY_FORMATTER));
        long dailyTtl = getSecondsUntilMidnight(now) + 3600; // 다음 자정 + 1시간 버퍼
        if (!checkAndIncrementAtomic(dailyKey, cost, properties.getDailyLimit(), dailyTtl)) {
            log.warn("일당 리미트 초과 - dailyKey: {}", dailyKey);
            // 시간당 증가분 롤백 (Lua Script가 실행되지 않았으므로 롤백 불필요)
            return false;
        }

        log.debug("Rate limit 체크 통과 - cost: {}", cost);
        return true;
    }

    /**
     * Lua Script로 원자적 체크 + 증가
     */
    private boolean checkAndIncrementAtomic(String key, int cost, int limit, long ttl) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(cost),
                String.valueOf(limit),
                String.valueOf(ttl)
        );

        return result != null && result == 1;
    }

    /**
     * Retry-After 계산: 다음 정시까지 남은 시간(초)
     */
    public long getSecondsUntilReset() {
        LocalDateTime now = LocalDateTime.now(ZONE_SEOUL);
        return getSecondsUntilNextHour(now);
    }

    /**
     * 다음 정시(00분)까지 남은 시간(초)
     */
    private long getSecondsUntilNextHour(LocalDateTime now) {
        LocalDateTime nextHour = now.plusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return Duration.between(now, nextHour).getSeconds();
    }

    /**
     * 다음 자정까지 남은 시간(초)
     */
    private long getSecondsUntilMidnight(LocalDateTime now) {
        LocalDateTime midnight = now.plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return Duration.between(now, midnight).getSeconds();
    }
}
