package com.climbers.hangangactivity.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.climbers.hangangactivity.mapper.UserMapper;
import com.climbers.hangangactivity.model.User;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;  // PasswordEncoder로 변경

    // 의존성 주입
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(String email, String password) {
        // 이메일 중복 체크
        if (userMapper.findByEmail(email) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(password);  // PasswordEncoder 사용

        // 사용자 저장
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        userMapper.insertUser(user);
    }
}
