package com.example.testsaytproyekt.users.controller;

import com.example.testsaytproyekt.users.dto.StudentResponseDto;
import com.example.testsaytproyekt.users.dto.StudentUpdateDto;
import com.example.testsaytproyekt.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> AllStudentds(){
       return ResponseEntity.status(HttpStatus.OK)
               .body(userService.getAll());
    }
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto>update(@PathVariable UUID id, @RequestBody StudentUpdateDto updateDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateStudent(id,updateDto));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok("Successfully deleted");
    }


}
