package com.animalshelter.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimitConfig {

    private final int requestsPerSecond;
    private final int burstCapacity;

    public RateLimitConfig(
            @Value("${gateway.rate-limit.requests-per-second}") int requestsPerSecond,
            @Value("${gateway.rate-limit.burst-capacity}") int burstCapacity
    ) {
        this.requestsPerSecond = requestsPerSecond;
        this.burstCapacity = burstCapacity;
    }

    public int getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }
}
