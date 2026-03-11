package com.animalshelter.animalregistry.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class UserContext {

    private String userId;
    private String username;
    private String role;
}
