package com.demo.album.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 회원(사용자) 엔티티
 * - JPA로 DB 테이블 "user"와 매핑
 * - 로그인 ID(username), 암호화된 비밀번호, 닉네임, 이메일, 역할(role) 보관
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** PK. DB에서 자동 증가 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /** 로그인에 사용하는 ID (중복 불가) */
    @Column(nullable = false, unique = true)
    private String username;

    /** BCrypt 등으로 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 표시용 닉네임 (중복 불가) */
    @Column(nullable = false, unique = true)
    private String nickname;

    /** 이메일 (중복 불가) */
    @Column(nullable = false, unique = true)
    private String email;

    /** 권한 역할. 예: "USER" → Spring Security에서 ROLE_USER로 사용 */
    @Column(nullable = false)
    @Builder.Default
    private String role = "USER";
}
