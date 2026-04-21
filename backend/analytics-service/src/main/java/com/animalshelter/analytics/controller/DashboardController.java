package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.config.UserContext;
import com.animalshelter.analytics.dto.DashboardResponse;
import com.animalshelter.analytics.exception.AccessDeniedException;
import com.animalshelter.analytics.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/reports")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserContext userContext;

    public DashboardController(DashboardService dashboardService, UserContext userContext) {
        this.dashboardService = dashboardService;
        this.userContext = userContext;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    private void requireRole(String... allowedRoles) {
        String currentRole = userContext.getRole();
        for (String role : allowedRoles) {
            if (role.equals(currentRole)) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied. Required role: " + String.join(" or ", allowedRoles));
    }
}
