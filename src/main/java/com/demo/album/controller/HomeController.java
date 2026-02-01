package com.demo.album.controller;

import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.dto.ApiResponse;
import com.demo.album.entity.User;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.service.AlbumService;
import com.demo.album.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 홈 API
 * - GET /api/home/{albumId}: 앨범 정보 + 내 앨범 여부 + 가능한 액션(viewAlbum/shareLink 또는 writeLetter)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final AlbumService albumService;
    private final UserService userService;

    /** 홈 화면: 앨범 정보 + isOwnAlbum + actions(내 앨범이면 viewAlbum/shareLink, 타인 앨범이면 writeLetter) */
    @GetMapping("/home/{albumId}")
    @Operation(summary = "홈 화면 조회", description = "내/타인 앨범 구분하여 홈 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHomePage(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        AlbumResponseDto album = albumService.getAlbumWithOwnership(albumId, user.getUserId());

        Map<String, Object> data = new java.util.HashMap<>(Map.of(
                "albumId", album.getAlbumId(),
                "title", album.getTitle(),
                "albumColor", album.getAlbumColor(),
                "visibility", album.isVisibility(),
                "letterCount", album.getLetterCount(),
                "isOwnAlbum", album.isOwnAlbum()
        ));

        data.put("actions", album.isOwnAlbum()
                ? List.of("viewAlbum", "shareLink")
                : List.of("writeLetter"));

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
