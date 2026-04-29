package com.example.testsaytproyekt.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyResetCodeDto {

    @NotBlank(message = "Telefon raqam majburiy")
    private String phoneNumber;

    @NotBlank(message = "Kod majburiy")
    private String code;
}