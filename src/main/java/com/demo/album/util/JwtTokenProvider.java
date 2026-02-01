package com.demo.album.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT 토큰 생성·검증·블랙리스트(로그아웃) 처리
 * - createToken: username을 subject로 넣어 1시간 유효 토큰 발급
 * - validateToken: 서명·만료 검증 (블랙리스트는 검사 안 함)
 * - invalidateToken: 로그아웃 시 토큰을 블랙리스트에 추가
 * - isTokenNotInvalidated: 블랙리스트에 없으면 true (필터에서 로그아웃된 토큰 거부용)
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private static final long VALIDITY_MS = 3600000L; // 1시간

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    /** 로그아웃된 토큰 목록 (동시 접근 안전) */
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    /** username을 subject로 넣어 JWT 발급 */
    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_MS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /** 토큰에서 subject(username) 추출 */
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** JWT 서명·만료 검증 (블랙리스트 미검사) */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /** 로그아웃 시 토큰 무효화 (블랙리스트 등록) */
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    /** 블랙리스트에 없으면 유효 (로그아웃된 토큰 아님) */
    public boolean isTokenNotInvalidated(String token) {
        return !invalidatedTokens.contains(token);
    }
}
