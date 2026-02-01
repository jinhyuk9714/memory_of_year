package com.demo.album.repository;

import com.demo.album.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 엔티티용 JPA Repository
 * - findByUsername: 로그인 ID로 사용자 조회 (중복 검사·인증용)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}