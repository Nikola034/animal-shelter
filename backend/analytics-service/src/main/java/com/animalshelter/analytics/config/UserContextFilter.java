package com.animalshelter.analytics.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class UserContextFilter implements Filter {

    private final ObjectProvider<UserContext> userContextProvider;

    public UserContextFilter(ObjectProvider<UserContext> userContextProvider) {
        this.userContextProvider = userContextProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;

        String userId = httpReq.getHeader("X-User-Id");
        String username = httpReq.getHeader("X-User-Username");
        String role = httpReq.getHeader("X-User-Role");

        if (userId != null) {
            UserContext ctx = userContextProvider.getObject();
            ctx.setUserId(userId);
            ctx.setUsername(username);
            ctx.setRole(role);
        }

        chain.doFilter(request, response);
    }
}
