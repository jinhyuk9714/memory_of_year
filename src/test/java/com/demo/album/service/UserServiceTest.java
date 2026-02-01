package com.demo.album.service;

import com.demo.album.entity.User;
import com.demo.album.exception.DuplicateResourceException;
import com.demo.album.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService 단위 테스트
 * - 회원가입, 로그인, loadUserByUsername 등 동작 검증
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String NICKNAME = "테스트";
    private static final String EMAIL = "test@example.com";

    @Nested
    @DisplayName("registerUser - 회원가입")
    class RegisterUser {

        @Test
        @DisplayName("새 사용자 가입 시 암호화 후 저장되고 User 반환")
        void success() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");

            User saved = User.builder()
                    .userId(1L)
                    .username(USERNAME)
                    .password("encodedPassword")
                    .nickname(NICKNAME)
                    .email(EMAIL)
                    .role("USER")
                    .build();
            when(userRepository.save(any(User.class))).thenReturn(saved);

            User result = userService.registerUser(USERNAME, PASSWORD, NICKNAME, EMAIL);

            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo(USERNAME);
            verify(userRepository).findByUsername(USERNAME);
            verify(passwordEncoder).encode(PASSWORD);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("이미 존재하는 username이면 DuplicateResourceException")
        void duplicateUsername() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mock(User.class)));

            assertThatThrownBy(() -> userService.registerUser(USERNAME, PASSWORD, NICKNAME, EMAIL))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("이미 존재하는 사용자 이름");

            verify(userRepository).findByUsername(USERNAME);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("authenticateUser - 로그인 인증")
    class AuthenticateUser {

        @Test
        @DisplayName("비밀번호 일치 시 Optional에 User 반환")
        void success() {
            User user = User.builder()
                    .userId(1L)
                    .username(USERNAME)
                    .password("encodedPassword")
                    .nickname(NICKNAME)
                    .email(EMAIL)
                    .role("USER")
                    .build();
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, "encodedPassword")).thenReturn(true);

            Optional<User> result = userService.authenticateUser(USERNAME, PASSWORD);

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo(USERNAME);
        }

        @Test
        @DisplayName("비밀번호 불일치 시 empty")
        void wrongPassword() {
            User user = User.builder().username(USERNAME).password("encoded").build();
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, "encoded")).thenReturn(false);

            Optional<User> result = userService.authenticateUser(USERNAME, PASSWORD);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 username이면 empty")
        void userNotFound() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            Optional<User> result = userService.authenticateUser(USERNAME, PASSWORD);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("loadUserByUsername - Spring Security용 사용자 조회")
    class LoadUserByUsername {

        @Test
        @DisplayName("존재하는 사용자면 UserDetails 반환 (권한 포함)")
        void success() {
            User user = User.builder()
                    .userId(1L)
                    .username(USERNAME)
                    .password("encoded")
                    .nickname(NICKNAME)
                    .email(EMAIL)
                    .role("USER")
                    .build();
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

            UserDetails details = userService.loadUserByUsername(USERNAME);

            assertThat(details.getUsername()).isEqualTo(USERNAME);
            assertThat(details.getPassword()).isEqualTo("encoded");
            assertThat(details.getAuthorities()).hasSize(1);
            assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("존재하지 않으면 UsernameNotFoundException")
        void notFound() {
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername(USERNAME))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining(USERNAME);
        }
    }

    @Nested
    @DisplayName("findByUsername")
    class FindByUsername {

        @Test
        @DisplayName("Repository 결과 그대로 반환")
        void returnsRepositoryResult() {
            User user = User.builder().userId(1L).username(USERNAME).build();
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

            Optional<User> result = userService.findByUsername(USERNAME);

            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo(1L);
        }
    }
}
