package com.demo.album.controller;

import com.demo.album.dto.ApiResponse;
import com.demo.album.dto.PhotoResponseDto;
import com.demo.album.dto.PhotoUploadRequestDto;
import com.demo.album.exception.InvalidRequestException;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.service.PhotoService;
import com.demo.album.service.StickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 사진 API (편지별 사진 업로드, 목록, 상세)
 * - multipart/form-data로 파일·코멘트·스티커 URL 전달
 */
@RestController
@RequestMapping("/api/letters/{letterId}/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final StickerService stickerService;

    /** 사진 업로드. 스티커 URL 검증 후 S3 업로드 + Photo 저장 */
    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "사진 업로드", description = "특정 편지에 사진을 업로드합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "사진 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "편지를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "파일 업로드 중 오류 발생")
    })
    public ResponseEntity<ApiResponse<PhotoResponseDto>> addPhoto(
            @PathVariable Long letterId,
            @ModelAttribute PhotoUploadRequestDto request) throws IOException {

        if (request.getStickerUrl() != null && !stickerService.isValidStickerUrl(request.getStickerUrl())) {
            throw new InvalidRequestException("유효하지 않은 스티커 URL입니다.");
        }

        var photo = photoService.addPhoto(letterId, request.getFile(), request.getComment(), request.getStickerUrl());
        var dto = new PhotoResponseDto(photo.getPhotoId(), photo.getUrl(), photo.getComment(), photo.getStickerUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
    }

    /** 편지별 사진 목록 */
    @GetMapping
    @Operation(summary = "사진 목록 조회", description = "특정 편지에 포함된 모든 사진을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사진 목록 조회 성공")
    public ResponseEntity<ApiResponse<List<PhotoResponseDto>>> getPhotosByLetterId(
            @Parameter(description = "사진을 조회할 편지의 ID", required = true) @PathVariable Long letterId) {

        var list = photoService.getPhotosByLetterId(letterId).stream()
                .map(p -> new PhotoResponseDto(p.getPhotoId(), p.getUrl(), p.getComment(), p.getStickerUrl()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /** 사진 상세. 없으면 ResourceNotFoundException → 404 */
    @GetMapping("/{photoId}")
    @Operation(summary = "사진 상세 조회", description = "특정 편지 내 특정 사진의 상세 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사진 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사진을 찾을 수 없습니다.")
    })
    public ResponseEntity<ApiResponse<PhotoResponseDto>> getPhoto(
            @Parameter(description = "사진을 조회할 편지의 ID", required = true) @PathVariable Long letterId,
            @Parameter(description = "조회할 사진의 ID", required = true) @PathVariable Long photoId) {

        var photo = photoService.getPhotoByIdAndLetterId(photoId, letterId)
                .orElseThrow(() -> new ResourceNotFoundException("사진을 찾을 수 없습니다."));

        var dto = new PhotoResponseDto(photo.getPhotoId(), photo.getUrl(), photo.getComment(), photo.getStickerUrl());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
