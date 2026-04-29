package com.example.testsaytproyekt.telegrambot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramVerifyDto {

    @NotBlank
    private String username;

    @NotBlank
    private String code;
}