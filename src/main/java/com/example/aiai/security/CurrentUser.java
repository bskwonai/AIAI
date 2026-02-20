package com.example.aiai.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public String userId(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return username != null ? username : jwt.getSubject();
    }
}
