package com.demo.album.controller;

import com.demo.album.dto.AlbumCreateRequestDto;
import com.demo.album.dto.AlbumResponseDto;
import com.demo.album.dto.ApiResponse;
import com.demo.album.entity.Album;
import com.demo.album.entity.User;
import com.demo.album.exception.InvalidRequestException;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.service.AlbumService;
import com.demo.album.service.StickerService;
import com.demo.album.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 앨범 API (생성, 조회, 수정)
 * - 인증 필요. @AuthenticationPrincipal로 현재 사용자 조회
 */
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final UserService userService;
    private final StickerService stickerService;

    /** 앨범 생성. 스티커 URL 검증 후 저장. 이미 앨범 있으면 DuplicateResourceException */
    @PostMapping("/create")
    @Operation(summary = "앨범 생성", description = "사용자가 새로운 앨범을 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "앨범 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 앨범이 존재합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 스티커 URL")
    })
    public ResponseEntity<ApiResponse<AlbumResponseDto>> createAlbum(
            @Parameter(description = "생성할 앨범 정보", required = true) @RequestBody AlbumCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        if (albumService.hasAlbum(user)) {
            throw new com.demo.album.exception.DuplicateResourceException("이미 앨범이 존재합니다.");
        }

        // stickerUrl이 있으면 허용 목록 검증, 없으면 null로 저장 (로컬/S3 미설정 대응)
        String stickerUrl = request.getStickerUrl();
        if (stickerUrl != null && !stickerUrl.isBlank() && !stickerService.isValidStickerUrl(stickerUrl)) {
            throw new InvalidRequestException("유효하지 않은 스티커 URL입니다.");
        }

        Album album = albumService.createAlbum(
                request.getTitle(),
                request.getAlbumColor(),
                request.getVisibility(),
                (stickerUrl != null && !stickerUrl.isBlank()) ? stickerUrl : null,
                user
        );

        AlbumResponseDto dto = new AlbumResponseDto(
                album.getAlbumId(),
                album.getTitle(),
                album.getAlbumColor(),
                album.isVisibility(),
                0,  // 새 앨범이므로 편지 0개 (lazy load 회피)
                album.getStickerUrl(),
                true  // 생성자는 본인 앨범
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
    }

    /** 앨범 조회. AlbumResponseDto (소유 여부 포함) 반환 */
    @GetMapping("/{albumId}")
    @Operation(summary = "앨범 조회", description = "앨범 ID로 앨범을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "앨범 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "앨범을 찾을 수 없습니다.")
    })
    public ResponseEntity<ApiResponse<AlbumResponseDto>> getAlbum(
            @Parameter(description = "조회할 앨범의 ID", required = true) @PathVariable Long albumId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        AlbumResponseDto dto = albumService.getAlbumWithOwnership(albumId, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /** 앨범 부분 수정. body에 title, albumColor, visibility, stickerUrl 등 포함 가능 */
    @PutMapping("/{albumId}")
    @Operation(summary = "앨범 업데이트", description = "앨범 ID로 앨범 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "앨범 업데이트 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "앨범을 찾을 수 없습니다.")
    })
    public ResponseEntity<ApiResponse<Album>> updateAlbum(
            @Parameter(description = "업데이트할 앨범의 ID", required = true) @PathVariable Long albumId,
            @Parameter(description = "업데이트할 필드와 값", required = true) @RequestBody Map<String, Object> updates) {

        Album updated = albumService.updateAlbum(albumId, updates);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}
