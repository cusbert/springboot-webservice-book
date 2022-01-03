package com.practice.book.config.auth;

import com.practice.book.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            // URL 별 권한 관리 설정 시작
            .authorizeRequests()
            .antMatchers("/", "/css/**", "/images/**", "/js/**", "/profile").permitAll() // 전체 허가
            .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // USER 권한만 허가
            .anyRequest().authenticated() // 나머지는 인증된 사용자(로그인 유저) 만 허가
            .and()
            // 로그아웃 설정 시작
            .logout()
            .logoutSuccessUrl("/") // 로그아웃 성공 시 / 이동
            .and()
            // OAuth 2로그인 기능 설정 시작
            .oauth2Login()
            .userInfoEndpoint() // OAuth 2 로그인 성공 이후 사용자 정보 가져올 때 설정 담당
            .userService(customOAuth2UserService); // 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스 구현체 등록

    }
}
