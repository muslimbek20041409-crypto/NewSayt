package com.example.testsaytproyekt.admin.controller;

import com.example.testsaytproyekt.admin.service.AdminService;
import com.example.testsaytproyekt.admin.dto.AdminCreateDto;
import com.example.testsaytproyekt.admin.dto.AdminResponseDto;
import com.example.testsaytproyekt.admin.dto.AdminUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AdminResponseDto>create(@RequestBody AdminCreateDto adminCreateDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(adminCreateDto));
    }
    @GetMapping
    public ResponseEntity<List<AdminResponseDto>>getAll(){
        return ResponseEntity.ok(adminService.getAll());
    }
    @DeleteMapping("/{id}")
    ResponseEntity<?>delete(@PathVariable UUID id) {
    adminService.delete(id);
    return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDto>update(@PathVariable UUID id, @RequestBody AdminUpdateDto updateDto){
        return ResponseEntity.ok(adminService.update(id,updateDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDto>getById(@PathVariable UUID id){
        return ResponseEntity.ok(adminService.getById(id));
    }
}
