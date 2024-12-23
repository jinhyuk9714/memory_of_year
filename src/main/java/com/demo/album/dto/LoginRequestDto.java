package com.demo.album.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public class LoginRequestDto {

    @Schema(description = "사용자의 아이디", example = "username123")
    private String username;

    @Schema(description = "사용자의 비밀번호", example = "password123")
    private String password;

    // Getter와 Setter 추가
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
