package com.sweetspot.server.user.DTO;

public class UserLoginResponseDTO {
    private Long userId;
    private String email;
    private String nickname;
    private String phoneNumber;
    private boolean isPhoneVerified;

    public UserLoginResponseDTO(Long userId, String email, String nickname, String phoneNumber, boolean isPhoneVerified) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.isPhoneVerified = isPhoneVerified;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isPhoneVerified() { return isPhoneVerified; }
    public void setPhoneVerified(boolean isPhoneVerified) { this.isPhoneVerified = isPhoneVerified; }
}
