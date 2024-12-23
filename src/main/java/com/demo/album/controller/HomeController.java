package com.demo.album.controller;

import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.entity.User;
import com.demo.album.service.AlbumService;
import com.demo.album.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final AlbumService albumService;
    private final UserService userService;

    @Autowired
    public HomeController(AlbumService albumService, UserService userService) {
        this.albumService = albumService;
        this.userService = userService;
    }

    // 홈 화면 조회 (내/타인 앨범 구분)
    @GetMapping("/home/{albumId}")
    public ResponseEntity<?> getHomePage(@PathVariable Long albumId) {
        // 현재 인증된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // UserService를 사용하여 username으로 userId를 찾음
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        Long userId = user.get().getUserId();
        AlbumResponseDto response = albumService.getAlbumWithOwnership(albumId, userId);

        boolean isOwner = response.isOwnAlbum();

        Map<String, Object> homePageResponse = new HashMap<>();
        homePageResponse.put("albumId", response.getAlbumId());
        homePageResponse.put("title", response.getTitle());
        homePageResponse.put("albumColor", response.getAlbumColor());
        homePageResponse.put("visibility", response.isVisibility());
        homePageResponse.put("letterCount", response.getLetterCount());
        homePageResponse.put("isOwnAlbum", isOwner);

        if (isOwner) {
            homePageResponse.put("actions", List.of("viewAlbum", "shareLink"));
        } else {
            homePageResponse.put("actions", List.of("writeLetter"));
        }

        return ResponseEntity.ok(homePageResponse);
    }
}

