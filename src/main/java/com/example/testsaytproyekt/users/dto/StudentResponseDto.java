package com.example.testsaytproyekt.users.dto;

import com.example.testsaytproyekt.enums.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponseDto {

    private UUID id;
    private String fullName;

    private Long telegramId;
    private String username;

    private Role role;
    private LocalDateTime createdAt;
}