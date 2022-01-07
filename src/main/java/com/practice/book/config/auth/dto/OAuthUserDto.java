package com.practice.book.config.auth.dto;

import com.practice.book.domain.user.Role;
import com.practice.book.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthUserDto {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthUserDto(Map<String, Object> attributes, String nameAttributeKey, String name,
        String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthUserDto of(String registrationId, String userNameAttributeName,
        // OAuth2User 에서 반환하는 사용자 정보는 Map 이라 하나씩 변환한다.
        Map<String, Object> attributes) {

        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthUserDto ofNaver(String userNameAttributeName,
        Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthUserDto.builder()
            .name((String) response.get("name"))
            .email((String) response.get("email"))
            .picture((String) response.get("profile_image"))
            .attributes(response)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    private static OAuthUserDto ofGoogle(String userNameAttributeName,
        Map<String, Object> attributes) {

        return OAuthUserDto.builder()
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .picture((String) attributes.get("picture"))
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }


    // OAuthUserDto 에서 엔티티를 생성하는 시점은 가입할 때이므로 Guest 권한 디폴트
    public User toEntity() {
        return User.builder()
            .name(name)
            .email(email)
            .picture(picture)
            .role(Role.GUEST)
            .build();
    }
}
