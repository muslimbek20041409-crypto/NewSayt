package com.example.testsaytproyekt.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDto {

    @NotBlank(message = "Telefon raqam majburiy")
    private String phoneNumber;
}