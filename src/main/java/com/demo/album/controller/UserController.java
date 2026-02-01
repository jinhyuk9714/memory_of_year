package com.demo.album.controller;

import com.demo.album.dto.ApiResponse;
import com.demo.album.dto.LoginRequestDto;
import com.demo.album.dto.RegisterRequestDto;
import com.demo.album.entity.User;
import com.demo.album.service.UserService;
import com.demo.album.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 API (회원가입, 로그인, 로그아웃)
 * - /api/auth/register: 회원가입 (비밀번호 암호화 후 저장)
 * - /api/auth/login: 로그인 (인증 성공 시 JWT 발급)
 * - /api/auth/logout: 로그아웃 (토큰 블랙리스트 등록)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /** 회원가입. 성공 시 201 + { success, data: { id, username, nickname, email } } */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 또는 사용자 이름 중복")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(
            @Parameter(description = "등록할 사용자 정보", required = true) @RequestBody RegisterRequestDto dto) {

        User user = userService.registerUser(
                dto.getUsername(),
                dto.getPassword(),
                dto.getNickname(),
                dto.getEmail()
        );

        Map<String, Object> data = Map.of(
                "id", user.getUserId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "email", user.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    /** 로그인. 성공 시 200 + { success, data: { token } }, 실패 시 401 + error */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 잘못되었습니다.")
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> loginUser(
            @Parameter(description = "로그인할 사용자 정보", required = true) @RequestBody LoginRequestDto dto) {

        return userService.authenticateUser(dto.getUsername(), dto.getPassword())
                .map(u -> {
                    String token = jwtTokenProvider.createToken(dto.getUsername());
                    return ResponseEntity.ok(ApiResponse.success(Map.of("token", token)));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("아이디 또는 비밀번호가 잘못되었습니다.")));
    }

    /** 로그아웃. Authorization: Bearer <token> 으로 전달한 토큰을 무효화 */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자가 로그아웃하여 JWT 토큰을 무효화합니다.")
    @ApiResponses(value = { @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공") })
    public ResponseEntity<ApiResponse<String>> logoutUser(
            @Parameter(description = "JWT 토큰", required = true) @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();
        jwtTokenProvider.invalidateToken(token);
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
    }
}
