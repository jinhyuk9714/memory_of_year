package com.demo.album.controller;

import com.demo.album.dto.PhotoResponseDto;
import com.demo.album.dto.PhotoUploadRequestDto;
import com.demo.album.service.PhotoService;
import com.demo.album.service.StickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/letters/{letterId}/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final StickerService stickerService;

    @Autowired
    public PhotoController(PhotoService photoService, StickerService stickerService) {
        this.photoService = photoService;
        this.stickerService = stickerService;
    }

    @Operation(summary = "사진 업로드", description = "특정 편지에 사진을 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사진 업로드 성공"),
            @ApiResponse(responseCode = "404", description = "편지를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "파일 업로드 중 오류 발생")
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PhotoResponseDto> addPhoto(
            @PathVariable Long letterId,
            @ModelAttribute PhotoUploadRequestDto request) {

        try {
            if (request.getStickerUrl() != null && !stickerService.isValidStickerUrl(request.getStickerUrl())) {
                return ResponseEntity.badRequest().body(null);
            }

            var photo = photoService.addPhoto(letterId, request.getFile(), request.getComment(), request.getStickerUrl());
            var responseDto = new PhotoResponseDto(photo.getPhotoId(), photo.getUrl(), photo.getComment(), photo.getStickerUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류 발생", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    @Operation(summary = "사진 목록 조회", description = "특정 편지에 포함된 모든 사진을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사진 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<PhotoResponseDto>> getPhotosByLetterId(
            @Parameter(description = "사진을 조회할 편지의 ID", required = true) @PathVariable Long letterId) {

        var photos = photoService.getPhotosByLetterId(letterId);
        var responseDtos = photos.stream()
                .map(photo -> new PhotoResponseDto(photo.getPhotoId(), photo.getUrl(), photo.getComment(), photo.getStickerUrl()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @Operation(summary = "사진 상세 조회", description = "특정 편지 내 특정 사진의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사진 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사진을 찾을 수 없습니다.")
    })
    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoResponseDto> getPhoto(
            @Parameter(description = "사진을 조회할 편지의 ID", required = true) @PathVariable Long letterId,
            @Parameter(description = "조회할 사진의 ID", required = true) @PathVariable Long photoId) {

        var photo = photoService.getPhotoByIdAndLetterId(photoId, letterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사진을 찾을 수 없습니다."));
        var responseDto = new PhotoResponseDto(photo.getPhotoId(), photo.getUrl(), photo.getComment(), photo.getStickerUrl());
        return ResponseEntity.ok(responseDto);
    }
}
