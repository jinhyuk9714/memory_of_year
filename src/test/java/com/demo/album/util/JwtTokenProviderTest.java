package com.demo.album.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtTokenProvider 단위 테스트
 * - 토큰 생성/파싱, 검증, 블랙리스트(로그아웃) 검증
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
    }

    @Nested
    @DisplayName("createToken / getUsername")
    class CreateAndParse {

        @Test
        @DisplayName("생성한 토큰에서 username 추출 가능")
        void success() {
            String username = "testuser";
            String token = jwtTokenProvider.createToken(username);

            assertThat(token).isNotBlank();
            String parsed = jwtTokenProvider.getUsername(token);
            assertThat(parsed).isEqualTo(username);
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("유효한 토큰이면 true")
        void valid() {
            String token = jwtTokenProvider.createToken("user");
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("잘못된 토큰이면 false")
        void invalid() {
            assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
        }
    }

    @Nested
    @DisplayName("invalidateToken / isTokenNotInvalidated")
    class Blacklist {

        @Test
        @DisplayName("로그아웃한 토큰은 isTokenNotInvalidated false")
        void invalidated() {
            String token = jwtTokenProvider.createToken("user");
            assertThat(jwtTokenProvider.isTokenNotInvalidated(token)).isTrue();

            jwtTokenProvider.invalidateToken(token);

            assertThat(jwtTokenProvider.isTokenNotInvalidated(token)).isFalse();
        }
    }
}
