package safelens.backend.global.ratelimit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateLimitStatusResponse {

    private int hourlyUsed;           // 시간당 사용 포인트
    private int hourlyLimit;          // 시간당 최대 포인트
    private int hourlyRemaining;      // 시간당 남은 포인트
    private long hourlyResetSeconds;  // 시간당 리셋까지 남은 초

    private int dailyUsed;            // 일당 사용 포인트
    private int dailyLimit;           // 일당 최대 포인트
    private int dailyRemaining;       // 일당 남은 포인트
    private long dailyResetSeconds;   // 일당 리셋까지 남은 초
}
