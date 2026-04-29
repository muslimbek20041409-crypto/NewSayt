package com.example.testsaytproyekt.admin.dto;

import com.example.testsaytproyekt.enums.Role;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminResponseDto {

    private UUID id;
    private String fullName;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}