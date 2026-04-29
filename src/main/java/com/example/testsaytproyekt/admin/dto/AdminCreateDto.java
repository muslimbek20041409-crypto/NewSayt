package com.example.testsaytproyekt.admin.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminCreateDto {

    @NotBlank
    private String username;

    @NotBlank
    private String fullName;



    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}