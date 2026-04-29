package com.example.testsaytproyekt.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCreateDto {

    @NotBlank
    private String fullName;



    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}