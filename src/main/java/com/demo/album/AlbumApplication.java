package com.demo.album;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Memory of Year 백엔드 애플리케이션 진입점
 * - Spring Boot 기동 시 JPA, Security, Web, Swagger 등 자동 설정
 */
@SpringBootApplication
public class AlbumApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlbumApplication.class, args);
	}
}
