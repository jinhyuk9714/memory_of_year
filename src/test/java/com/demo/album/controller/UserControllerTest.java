package com.demo.album.controller;

import com.demo.album.dto.ApiResponse;
import com.demo.album.entity.User;
import com.demo.album.service.UserService;
import com.demo.album.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController API 테스트 (MockMvc)
 * - 회원가입/로그인/로그아웃 엔드포인트 검증 (Service·JWT Mock)
 */
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화하여 컨트롤러만 테스트
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("회원가입 성공 시 201 + success true + id/username/nickname/email")
        void success() throws Exception {
            User saved = User.builder()
                    .userId(1L)
                    .username("user1")
                    .nickname("닉네임")
                    .email("a@b.com")
                    .build();
            when(userService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(saved);

            String body = "{\"username\":\"user1\",\"password\":\"pass\",\"nickname\":\"닉네임\",\"email\":\"a@b.com\"}";

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("user1"))
                    .andExpect(jsonPath("$.data.nickname").value("닉네임"))
                    .andExpect(jsonPath("$.data.email").value("a@b.com"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("인증 성공 시 200 + token")
        void success() throws Exception {
            User user = User.builder().userId(1L).username("user1").password("encoded").build();
            when(userService.authenticateUser("user1", "pass")).thenReturn(Optional.of(user));
            when(jwtTokenProvider.createToken("user1")).thenReturn("jwt.token.here");

            String body = "{\"username\":\"user1\",\"password\":\"pass\"}";

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value("jwt.token.here"));
        }

        @Test
        @DisplayName("인증 실패 시 401 + error")
        void failure() throws Exception {
            when(userService.authenticateUser(anyString(), anyString())).thenReturn(Optional.empty());

            String body = "{\"username\":\"user1\",\"password\":\"wrong\"}";

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {

        @Test
        @DisplayName("Authorization 헤더로 토큰 전달 시 200 + 메시지")
        void success() throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                            .with(csrf())
                            .header("Authorization", "Bearer my.token.here"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value("로그아웃되었습니다."));
        }
    }
}
