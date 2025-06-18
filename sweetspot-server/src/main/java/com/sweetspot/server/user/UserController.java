package com.sweetspot.server.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import com.sweetspot.server.user.DTO.UserInfoResponseDTO;
import com.sweetspot.server.user.DTO.UserLoginRequestDTO;
import com.sweetspot.server.user.DTO.UserLoginResponseDTO;
import com.sweetspot.server.user.DTO.UserRegisterDTO;
import com.sweetspot.server.user.DTO.UserIdRequestDTO;
import com.sweetspot.server.user.DTO.UserRegisterPhoneNumberCheckRequestDTO;
import com.sweetspot.server.user.DTO.check.UserPasswordCheckRequestDTO;
import com.sweetspot.server.user.DTO.check.UserPasswordCheckResponseDTO;
import com.sweetspot.server.user.DTO.check.UserRegisterEmailCheckRequestDTO;
import com.sweetspot.server.user.DTO.update.UserImageUpdateDTO;
import com.sweetspot.server.user.DTO.update.UserNicknameUpdateDTO;
import com.sweetspot.server.user.DTO.update.UserPasswordUpdateRequestDTO;
import com.sweetspot.server.user.DTO.update.UserPasswordUpdateResponseDTO;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data") // <-- consumes 속성 추가!
    public ResponseEntity<?> registerUser(
            @RequestPart("data") UserRegisterDTO userDTO, // <-- "data" 파트에서 DTO를 받음
            @RequestPart(value = "profileImage", required = false) MultipartFile file) { // <-- "profileImage" 파트에서 파일을 받음 (필수 아님)
        try {
            // 파일을 처리하는 로직 (PinImageController의 파일 업로드 로직과 유사)
            String profileImageUrl = null;
            String profileImageName = null;

            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/profile-images/"; // 실제 서버 경로에 맞게 설정
                java.nio.file.Files.createDirectories(Paths.get(uploadDir));

                String originalFilename = file.getOriginalFilename();
                String ext = "";
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex != -1) {
                    ext = originalFilename.substring(dotIndex);
                }
                String newFileName = UUID.randomUUID().toString() + ext;
                Path savePath = Paths.get(uploadDir + newFileName);
                java.nio.file.Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

                // 서버에서 접근 가능한 URL로 설정 (BASE_URL과 일치시켜야 함)
                // "http://localhost:8080/images/profile-images/" 대신
                // "http://[PC의 실제 IP]:8080/images/profile-images/" 또는 상대경로로 설정해야 물리 기기에서 접근 가능
                profileImageUrl = "http://localhost:8080/images/profile-images/" + newFileName; // 개발 환경에서 localhost 사용
                profileImageName = newFileName;
            }

            // UserRegisterDTO에 이미지 정보 설정 (업로드된 경우)
            userDTO.setProfileImageUrl(profileImageUrl);
            userDTO.setProfileImageName(profileImageName);

            UserEntity savedUser = userService.registerUser(userDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            // 파일 처리 중 발생한 예외
            Map<String, String> error = new HashMap<>();
            error.put("message", "프로필 이미지 저장 실패: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register/check/email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestBody UserRegisterEmailCheckRequestDTO emailDTO) {
        Map<String, String> response = new HashMap<>();
        
        boolean exists = userService.existsByEmail(emailDTO.getEmail());
        if (exists) {
            response.put("message", "이미 사용 중인 이메일입니다.");
            response.put("available", "false");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        response.put("message", "사용 가능한 이메일입니다.");
        response.put("available", "true");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register/check/phone")
    public ResponseEntity<?> checkPhoneDuplicate(@RequestBody UserRegisterPhoneNumberCheckRequestDTO phoneDTO) {
        Map<String, String> response = new HashMap<>();

        boolean exists = userService.existsByPhoneNumber(phoneDTO.getPhoneNumber());
        if (exists) {
            response.put("message", "이미 사용 중인 전화번호입니다.");
            response.put("available", "false");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        response.put("message", "사용 가능한 전화번호입니다.");
        response.put("available", "true");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequestDTO loginRequest, HttpSession session) {
        UserEntity user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이메일 또는 비밀번호가 잘못되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        session.setAttribute("user", user.getUserId());

        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO(
            user.getUserId(),
            user.getEmail(),
            user.getNickname(),
            user.getPhoneNumber(),
            user.isPhoneVerified(),
            user.getCreatedAt(),
            user.getProfileImageUrl()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpSession session) {
        Map<String, String> response = new HashMap<>();

        if (session.getAttribute("user") != null) {
            session.invalidate();
            response.put("message", "로그아웃 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "이미 로그아웃된 상태입니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping(value = "/update/image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestPart("user") UserImageUpdateDTO userData,
            @RequestPart("file") MultipartFile file) {

        try {
            String uploadDir = "uploads/profile-images/";
            String originalFilename = file.getOriginalFilename();

            String ext = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex != -1) {
                ext = originalFilename.substring(dotIndex);
            }

            String newFileName = UUID.randomUUID().toString() + ext;
            Path savePath = Paths.get(uploadDir + newFileName);

            Files.createDirectories(savePath.getParent());
            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://localhost:8080/images/profile-images/" + newFileName;

            userService.saveUserProfileImageUrl(userData.getUserId(), fileUrl, newFileName, uploadDir);

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("userId", String.valueOf(userData.getUserId()));

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/image/delete")
    public ResponseEntity<Map<String, String>> deleteProfileImage(@RequestBody UserImageUpdateDTO userData) {
        try {
            String uploadDir = "uploads/profile-images/";
            userService.deleteUserProfileImage(userData.getUserId(), uploadDir);

            Map<String, String> response = new HashMap<>();
            response.put("message", "프로필 이미지가 성공적으로 삭제되었습니다.");
            response.put("userId", String.valueOf(userData.getUserId()));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/update/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody UserNicknameUpdateDTO dto) {
        try {
            UserEntity updatedUser = userService.updateNickname(dto.getUserId(), dto.getNewNickname());

            Map<String, String> response = new HashMap<>();
            response.put("message", "닉네임이 성공적으로 변경되었습니다.");
            response.put("userId", String.valueOf(updatedUser.getUserId()));
            response.put("nickname", updatedUser.getNickname());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/check/password")
    public ResponseEntity<UserPasswordCheckResponseDTO> checkPassword(@RequestBody UserPasswordCheckRequestDTO request) {
        try {
            boolean isMatch = userService.checkPassword(request.getUserId(), request.getPassword());

            if (isMatch) {
                return ResponseEntity.ok(new UserPasswordCheckResponseDTO(true, "비밀번호가 일치합니다."));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserPasswordCheckResponseDTO(false, "비밀번호가 일치하지 않습니다."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserPasswordCheckResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/update/password")
    public ResponseEntity<UserPasswordUpdateResponseDTO> updatePassword(
            @RequestBody UserPasswordUpdateRequestDTO request) {

        try {
            userService.updatePassword(request.getUserId(), request.getNewPassword());
            return ResponseEntity.ok(new UserPasswordUpdateResponseDTO(true, "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserPasswordUpdateResponseDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserPasswordUpdateResponseDTO(false, "비밀번호 변경 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestBody UserIdRequestDTO request) {
        try {
            UserInfoResponseDTO userInfo = userService.getUserInfoWithPosts(request.getUserId());
            return ResponseEntity.ok(userInfo);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}