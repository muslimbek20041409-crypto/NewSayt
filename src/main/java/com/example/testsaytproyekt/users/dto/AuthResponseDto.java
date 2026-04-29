package com.example.testsaytproyekt.users.dto;

import com.example.testsaytproyekt.enums.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDto {

    private UUID id;
    private Long telegramId;   // bot bilan bog‘langan id
    private String fullName;
    private String username;
    private Role role;
    private String token;      // JWT token (frontend uchun)
}