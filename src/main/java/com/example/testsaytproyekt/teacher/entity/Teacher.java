package com.example.testsaytproyekt.teacher.entity;

import com.example.testsaytproyekt.enums.Role;
import com.example.testsaytproyekt.test.entity.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Teacher implements UserDetails {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private Long telegramId;

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Phone number must be valid")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean verified = false;

    @JsonIgnore
    private String verificationCode;

    @JsonIgnore
    private LocalDateTime codeExpiryTime;

    @JsonIgnore
    private String resetCode;

    @JsonIgnore
    private LocalDateTime resetCodeExpiryTime;

    @JsonIgnore
    private Boolean resetVerified = false;

    @JsonIgnore
    @Column(nullable = false)
    private int failedAttempts = 0;

    @JsonIgnore
    private LocalDateTime lockTime;

    @Column(nullable = false)
    private boolean enabled = true;

    @JsonIgnore
    @Column(nullable = false)
    private boolean accountNonExpired = true;

    @JsonIgnore
    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    @JsonIgnore
    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.TEACHER;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Test> tests;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (failedAttempts < 0) {
            failedAttempts = 0;
        }
        if (resetVerified == null) {
            resetVerified = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @JsonIgnore
    public String getFullName() {
        return fullName;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        if (lockTime != null) {
            Duration lockedDuration = Duration.between(lockTime, LocalDateTime.now());
            if (lockedDuration.toHours() >= 24) {
                resetFailedAttempts();
            }
        }
        return accountNonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return enabled && verified;
    }

    public void registerFailedAttempt(int maxFailedAttempts) {
        failedAttempts++;
        if (failedAttempts >= maxFailedAttempts) {
            accountNonLocked = false;
            lockTime = LocalDateTime.now();
        }
    }

    public void resetFailedAttempts() {
        failedAttempts = 0;
        lockTime = null;
        accountNonLocked = true;
    }

    public void unlock() {
        resetFailedAttempts();
        enabled = true;
    }

    @JsonIgnore
    public boolean isCurrentlyLocked() {
        return !isAccountNonLocked();
    }
}