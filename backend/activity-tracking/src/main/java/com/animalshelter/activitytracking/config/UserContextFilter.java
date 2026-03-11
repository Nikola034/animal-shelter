package com.animalshelter.activitytracking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class UserContextFilter extends OncePerRequestFilter {

    private final ObjectProvider<UserContext> userContextProvider;

    public UserContextFilter(ObjectProvider<UserContext> userContextProvider) {
        this.userContextProvider = userContextProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Username");
        String role = request.getHeader("X-User-Role");

        if (userId != null && username != null && role != null) {
            UserContext ctx = userContextProvider.getObject();
            ctx.setUserId(userId);
            ctx.setUsername(username);
            ctx.setRole(role);
        }

        filterChain.doFilter(request, response);
    }
}
