package com.demo.album.filter;

import com.demo.album.service.UserService;
import com.demo.album.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * JWT 인증 필터
 * - Authorization: Bearer <token> 헤더가 있으면 토큰 검증
 * - validateToken(서명·만료) + isTokenNotInvalidated(로그아웃 블랙리스트) 둘 다 통과 시
 *   UserDetails를 로드해 SecurityContext에 인증 정보 설정
 * - Swagger 경로는 필터 로직 건너뜀
 */
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestUrl = httpRequest.getRequestURI();

        if (requestUrl.startsWith("/swagger-ui") || requestUrl.startsWith("/v3/api-docs")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = authorizationHeader.substring(7).trim();
        if (jwtTokenProvider.validateToken(token) && jwtTokenProvider.isTokenNotInvalidated(token)) {
            String username = jwtTokenProvider.getUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
