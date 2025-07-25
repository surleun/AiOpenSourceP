package com.sweetspot.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email); // 사용자 이메일로 사용자 찾기
    UserEntity findByPhoneNumber(String phoneNumber);// 사용자 전화번호로 사용자 찾기

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
