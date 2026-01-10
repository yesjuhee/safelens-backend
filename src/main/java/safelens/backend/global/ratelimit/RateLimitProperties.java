package safelens.backend.global.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private int hourlyLimit = 400;
    private int dailyLimit = 2000;
    private ApiCosts apiCosts = new ApiCosts();

    @Getter
    @Setter
    public static class ApiCosts {

        private int upload = 0;
        private int detect = 1;
        private int edit = 20;
    }
}
