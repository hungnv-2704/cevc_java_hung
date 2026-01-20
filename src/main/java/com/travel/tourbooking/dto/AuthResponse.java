package com.travel.tourbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String name;
    
    public AuthResponse(String token, Long userId, String username, String email, String name) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
    }
}
