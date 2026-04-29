package com.example.testsaytproyekt.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {

    @NotBlank(message = "Telefon raqam majburiy")
    private String phoneNumber;

    @NotBlank(message = "Yangi parol majburiy")
    @Size(min = 4, message = "Parol kamida 4 ta belgidan iborat bo‘lsin")
    private String newPassword;
}