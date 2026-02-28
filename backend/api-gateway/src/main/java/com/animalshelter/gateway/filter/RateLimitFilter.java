package com.animalshelter.gateway.filter;

import com.animalshelter.gateway.config.RateLimitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimitConfig rateLimitConfig;
    private final Map<String, ClientRateInfo> clientRates = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = getClientIp(exchange);

        ClientRateInfo rateInfo = clientRates.computeIfAbsent(clientIp, k -> new ClientRateInfo());

        long now = System.currentTimeMillis();
        long windowStart = rateInfo.windowStart.get();

        // Reset window if more than 1 second has passed
        if (now - windowStart > 1000) {
            rateInfo.windowStart.set(now);
            rateInfo.requestCount.set(1);
            return chain.filter(exchange);
        }

        int currentCount = rateInfo.requestCount.incrementAndGet();

        if (currentCount > rateLimitConfig.getBurstCapacity()) {
            log.warn("Rate limit exceeded for client: {}", clientIp);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -2;
    }

    private static class ClientRateInfo {
        final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        final AtomicInteger requestCount = new AtomicInteger(0);
    }
}
