package com.demo.album.controller;

import com.demo.album.dto.AlbumCreateRequestDto;
import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import com.demo.album.service.AlbumService;
import com.demo.album.service.StickerService;
import com.demo.album.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final UserService userService;
    private final StickerService stickerService;

    @Autowired
    public AlbumController(AlbumService albumService, UserService userService, StickerService stickerService) {
        this.albumService = albumService;
        this.userService = userService;
        this.stickerService = stickerService;
    }

    @PostMapping("/create")
    @Operation(summary = "앨범 생성", description = "사용자가 새로운 앨범을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "앨범 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "409", description = "이미 앨범이 존재합니다."),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 스티커 URL")
    })
    public ResponseEntity<?> createAlbum(
            @Parameter(description = "생성할 앨범 정보", required = true) @RequestBody AlbumCreateRequestDto request) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        if (albumService.hasAlbum(user.get())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 앨범이 존재합니다.");
        }

        // 스티커 URL 검증
        if (!stickerService.isValidStickerUrl(request.getStickerUrl())) {
            return ResponseEntity.badRequest().body("유효하지 않은 스티커 URL입니다.");
        }

        Album album = albumService.createAlbum(
                request.getTitle(),
                request.getAlbumColor(),
                request.getVisibility(),
                request.getStickerUrl(),
                user.get()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(album);
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "앨범 조회", description = "앨범 ID로 앨범을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "앨범 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "앨범을 찾을 수 없습니다.")
    })
    public ResponseEntity<?> getAlbum(
            @Parameter(description = "조회할 앨범의 ID", required = true) @PathVariable Long albumId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        try {
            AlbumResponseDto albumData = albumService.getAlbumWithOwnership(albumId, user.get().getUserId());
            return ResponseEntity.ok(albumData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{albumId}")
    @Operation(summary = "앨범 업데이트", description = "앨범 ID로 앨범 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "앨범 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "앨범을 찾을 수 없습니다.")
    })
    public ResponseEntity<?> updateAlbum(
            @Parameter(description = "업데이트할 앨범의 ID", required = true) @PathVariable Long albumId,
            @Parameter(description = "업데이트할 필드와 값", required = true) @RequestBody Map<String, Object> updates) {
        try {
            Album updatedAlbum = albumService.updateAlbum(albumId, updates);
            return ResponseEntity.ok(updatedAlbum);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("앨범을 찾을 수 없습니다.");
        }
    }

}
