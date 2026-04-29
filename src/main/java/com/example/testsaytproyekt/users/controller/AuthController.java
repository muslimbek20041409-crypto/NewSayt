package com.example.testsaytproyekt.users.controller;

import com.example.testsaytproyekt.jwt.JwtService;
import com.example.testsaytproyekt.telegrambot.dto.TelegramVerifyDto;
import com.example.testsaytproyekt.users.dto.*;
import com.example.testsaytproyekt.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public ResponseEntity<StudentResponseDto> signUp(@RequestBody @Valid StudentCreateDto dto) {
        StudentResponseDto responseDto = userService.signUp(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid TelegramVerifyDto dto) {
        userService.verifyTelegram(dto);
        return ResponseEntity.ok("Account verified successfully");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> signIn(@RequestBody @Valid StudentSignInDto dto) {
        AuthResponseDto authResponseDto = userService.signIn(dto);

        String token = jwtService.generateToken(
                authResponseDto.getUsername(),
                Map.of("role", authResponseDto.getRole().name())
        );

        authResponseDto.setToken(token);

        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordDto dto) {
        userService.forgotPassword(dto);
        return ResponseEntity.ok("Parolni tiklash kodi Telegram botga yuborildi");
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody @Valid VerifyResetCodeDto dto) {
        userService.verifyResetCode(dto);
        return ResponseEntity.ok("Kod tasdiqlandi. Endi yangi parol qo‘yishingiz mumkin");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDto dto) {
        userService.resetPassword(dto);
        return ResponseEntity.ok("Parol muvaffaqiyatli yangilandi");
    }
}