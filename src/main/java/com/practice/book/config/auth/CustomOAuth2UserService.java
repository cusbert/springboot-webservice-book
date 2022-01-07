package com.practice.book.config.auth;

import com.practice.book.config.auth.dto.OAuthUserDto;
import com.practice.book.config.auth.dto.SessionUser;
import com.practice.book.domain.user.User;
import com.practice.book.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인 진행 중인 서비스 구분 코드
        // 구글인지 카카오인지 네이버인지 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 키가 되는 필드 값
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

        // OAuth2UserService를 통해 가지고 온 OAuth2User의 attribute를 담을 클래스
        OAuthUserDto oAuthUserDto = OAuthUserDto.of(registrationId, userNameAttributeName,
            oAuth2User.getAttributes());

        User user = saveOrUpdate(oAuthUserDto);
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
            oAuthUserDto.getAttributes(),
            oAuthUserDto.getNameAttributeKey());
    }

    // 사용자 정보 업데이트 대비
    private User saveOrUpdate(OAuthUserDto oAuthUserDto) {
        User user = userRepository.findByEmail(oAuthUserDto.getEmail())
            .map(entity -> entity.update(oAuthUserDto.getName(), oAuthUserDto.getPicture()))
            .orElse(oAuthUserDto.toEntity());

        return userRepository.save(user);
    }
}
