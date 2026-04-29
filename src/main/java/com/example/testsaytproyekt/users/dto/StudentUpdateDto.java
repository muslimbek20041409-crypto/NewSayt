package com.example.testsaytproyekt.users.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentUpdateDto {

    private String fullName;


    // 🔥 Telegram username (update qilish mumkin)
    private String username;

    public String phoneNumber;
}