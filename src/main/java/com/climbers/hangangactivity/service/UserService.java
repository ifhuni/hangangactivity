package com.climbers.hangangactivity.service;

import com.climbers.hangangactivity.model.User;
import com.climbers.hangangactivity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String email, String password, String name, String phone, String role) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 객체 생성
        User user = new User(email, encodedPassword, name, phone, role);

        // 사용자 저장
        userRepository.save(user);
    }
}
