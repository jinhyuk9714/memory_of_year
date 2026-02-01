package com.demo.album.service;

import com.demo.album.entity.User;
import com.demo.album.exception.DuplicateResourceException;
import com.demo.album.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 회원(사용자) 비즈니스 로직
 * - UserDetailsService 구현: Spring Security가 loadUserByUsername으로 인증 시 사용
 * - 회원가입(암호화), 로그인 인증, username 조회, 현재 사용자 닉네임 조회
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** 회원가입: username 중복 시 DuplicateResourceException, 비밀번호 암호화 후 저장 */
    public User registerUser(String username, String password, String nickname, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateResourceException("이미 존재하는 사용자 이름입니다.");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .role("USER")
                .build();
        return userRepository.save(user);
    }

    /** 로그인 인증: username·password 일치 시 Optional에 User, 아니면 empty */
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        List<SimpleGrantedAuthority> authorities = Optional.ofNullable(user.getRole())
                .map(r -> List.of(new SimpleGrantedAuthority("ROLE_" + r)))
                .orElse(Collections.emptyList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    /** username으로 User 조회 (컨트롤러 등에서 인증 사용자 엔티티 조회용) */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** 현재 SecurityContext에 있는 사용자의 nickname 반환 (인증 필요) */
    public String getCurrentUserNickname() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(User::getNickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }
}
