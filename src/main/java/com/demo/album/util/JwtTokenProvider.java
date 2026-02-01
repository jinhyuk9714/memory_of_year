package com.demo.album.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final long VALIDITY_MS = 3600000L; // 1시간

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

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
