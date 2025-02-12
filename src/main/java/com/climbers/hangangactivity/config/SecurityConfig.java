package com.climbers.hangangactivity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // HttpSecurity 설정
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests() 
                .requestMatchers("/css/**", "/js/**", "/images/**","/scss/**","/vendor/**").permitAll()  // 정적 리소스는 인증 없이 허용
                .requestMatchers("/company/register").permitAll()   // 회원가입 페이지는 누구나 접근 가능
                .requestMatchers("/company/login").permitAll()   // 회원가입 페이지는 누구나 접근 가능
                .requestMatchers("/company/**").authenticated()     // /company/** 경로는 인증된 사용자만 접근
                .requestMatchers("/user/**").permitAll()            // /user/** 경로는 누구나 접근 가능
                .anyRequest().permitAll()                                       // 나머지 경로는 모두 허용
            .and()
            .formLogin()
                .loginPage("/company/login")                          // 로그인 페이지를 /company/login으로 설정
                .loginProcessingUrl("/login")                // 로그인 처리 URL (선택 사항)
                .defaultSuccessUrl("/company", true)// 로그인 성공 후 /company로 리다이렉트
                .permitAll()  // 로그인 페이지는 누구나 접근 가능
            .and()
            .logout()
                .permitAll();  // 로그아웃 페이지는 누구나 접근 가능

        return http.build();
    }

    // 사용자 인증 정보를 메모리 내에 저장
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // company용 사용자 추가
        manager.createUser(User.withUsername("companyUser")
                .password("{noop}companyPass")  // {noop}는 평문 비밀번호를 사용
                .roles("COMPANY")  // 'COMPANY' 역할
                .build());
        
        // user용 사용자 추가
        manager.createUser(User.withUsername("normalUser")
                .password("{noop}userPass")  // 평문 비밀번호 사용
                .roles("USER")  // 'USER' 역할
                .build());
        
        return manager;
    }

    // BCryptPasswordEncoder 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCryptPasswordEncoder를 빈으로 등록
    }
}
