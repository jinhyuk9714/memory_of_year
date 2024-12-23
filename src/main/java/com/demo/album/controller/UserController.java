package com.demo.album.controller;

import com.demo.album.dto.LoginRequestDto;
import com.demo.album.dto.RegisterRequestDto;
import com.demo.album.entity.User;
import com.demo.album.service.UserService;
import com.demo.album.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 사용자 이름 중복")
    })
    public ResponseEntity<Map<String, Object>> registerUser(
            @Parameter(description = "등록할 사용자 정보", required = true) @RequestBody RegisterRequestDto registerRequestDto) {
        try {
            User registeredUser = userService.registerUser(
                    registerRequestDto.getUsername(),
                    registerRequestDto.getPassword(),
                    registerRequestDto.getNickname(),
                    registerRequestDto.getEmail()
            );
            Map<String, Object> response = new HashMap<>();
            response.put("id", registeredUser.getUserId());
            response.put("username", registeredUser.getUsername());
            response.put("nickname", registeredUser.getNickname());
            response.put("email", registeredUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 잘못되었습니다.")
    })
    public ResponseEntity<Map<String, String>> loginUser(
            @Parameter(description = "로그인할 사용자 정보", required = true) @RequestBody LoginRequestDto loginRequestDto) {
        Optional<User> authenticatedUser = userService.authenticateUser(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        if (authenticatedUser.isPresent()) {
            String token = jwtTokenProvider.createToken(loginRequestDto.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자가 로그아웃하여 JWT 토큰을 무효화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    public ResponseEntity<String> logoutUser(
            @Parameter(description = "JWT 토큰", required = true) @RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.replace("Bearer ", "");
        jwtTokenProvider.invalidateToken(tokenWithoutBearer);
        return ResponseEntity.ok("로그아웃되었습니다.");
    }
}
