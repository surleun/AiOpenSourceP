package com.sweetspot.server.user;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.sweetspot.server.user.DTO.UserLoginRequestDTO;
import com.sweetspot.server.user.DTO.UserLoginResponseDTO;
import com.sweetspot.server.user.DTO.UserRegisterDTO;
import com.sweetspot.server.user.DTO.UserRegisterEmailCheckRequestDTO;
// import com.sweetspot.server.user.DTO.UserRegisterErrorResponseDTO;
import com.sweetspot.server.user.DTO.UserRegisterPhoneNumberCheckRequestDTO;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    //@Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerUser(@RequestBody UserRegisterDTO userDTO) {
        try {
            UserEntity userEntity = userService.registerUser(userDTO);
            return new ResponseEntity<>(userEntity, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    //회원가입, 이메일 중복 확인
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

    //회원가입, 전화번호 중복 확인
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

        // 비밀번호를 제외한 정보로 응답 DTO 구성
        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO(
            user.getUserId(),
            user.getEmail(),
            user.getNickname(),
            user.getPhoneNumber(),
            user.isPhoneVerified(),
            user.getCreatedAt()
        );

        return ResponseEntity.ok(responseDTO);
    }


    /*
    // 이메일로 사용자 조회 API
    @GetMapping("/email/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email) {
        UserEntity userEntity = userService.getUserByEmail(email);
        if (userEntity != null) {
            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 전화번호로 사용자 조회 API
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<UserEntity> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        UserEntity userEntity = userService.getUserByPhoneNumber(phoneNumber);
        if (userEntity != null) {
            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 사용자 정보 업데이트 API
    @PutMapping("/{userId}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        try {
            UserEntity updatedUser = userService.updateUser(userId, userDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
     */
}
