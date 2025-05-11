package com.sweetspot.server.user.DTO;

import java.time.LocalDateTime;

public class UserLoginResponseDTO {
    private Long userId;
    private String email;
    private String nickname;
    private String phoneNumber;
    private boolean phoneVerified;
    private LocalDateTime createdAt;

    // 생성자
    public UserLoginResponseDTO(Long userId, String email, String nickname, String phoneNumber, boolean phoneVerified, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.phoneVerified = phoneVerified;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isPhoneVerified() { return phoneVerified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}