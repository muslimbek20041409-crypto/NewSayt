package com.example.testsaytproyekt.admin.service;

import com.example.testsaytproyekt.admin.repository.AdminRepository;
import com.example.testsaytproyekt.admin.dto.AdminCreateDto;
import com.example.testsaytproyekt.admin.dto.AdminResponseDto;
import com.example.testsaytproyekt.admin.dto.AdminUpdateDto;
import com.example.testsaytproyekt.admin.entity.Admin;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper=new ModelMapper();
   private final AdminRepository adminRepository;
    public AdminResponseDto create(AdminCreateDto adminCreateDto) {
        String encode = passwordEncoder.encode(adminCreateDto.getPassword());
        Admin map = mapper.map(adminCreateDto, Admin.class);
        map.setPassword(encode);
        Admin save = adminRepository.save(map);
        return mapper.map(save,AdminResponseDto.class);

    }

    public List<AdminResponseDto> getAll() {
       return adminRepository.findAll().stream().
               map(admin -> mapper.map(admin,AdminResponseDto.class))
               .toList();
    }

    public void delete(UUID id) {
        adminRepository.deleteById(id);
    }

    public AdminResponseDto update(UUID id, AdminUpdateDto updateDto) {
        Admin admin = adminRepository.findById(id).get();
        mapper.map(updateDto,admin);
        Admin save = adminRepository.save(admin);
        return mapper.map(save,AdminResponseDto.class);
    }

    public AdminResponseDto getById(UUID id) {
        Admin admin = adminRepository.findById(id).get();
        return mapper.map(admin,AdminResponseDto.class);
    }
}
