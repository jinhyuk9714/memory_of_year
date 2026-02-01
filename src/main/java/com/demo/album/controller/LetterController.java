package com.demo.album.controller;

import com.demo.album.dto.LetterListResponseDto;
import com.demo.album.dto.LetterRequestDto;
import com.demo.album.dto.LetterResponseDto;
import com.demo.album.dto.ApiResponse;
import com.demo.album.entity.Letter;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.service.LetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 편지 API (앨범별 편지 생성, 목록, 상세)
 * - /api/albums/{albumId}/create: 편지 작성
 * - /api/albums/{albumId}/letters: 편지 목록
 * - /api/albums/{albumId}/letters/{letterId}: 편지 상세
 */
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;

    /** 편지 생성. LetterRequestDto로 제목/작성자/내용/익명/색상 전달 */
    @PostMapping("/{albumId}/create")
    @Operation(summary = "편지 생성", description = "특정 앨범에 새 편지를 작성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "편지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "앨범을 찾을 수 없습니다.")
    })
    public ResponseEntity<ApiResponse<LetterListResponseDto>> createLetter(
            @PathVariable Long albumId,
            @RequestBody LetterRequestDto letterRequest) {

        Letter letter = letterService.createLetter(albumId, letterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(new LetterListResponseDto(letter)));
    }

    /** 앨범별 편지 목록 (LetterListResponseDto 리스트, photoCount 포함) */
    @GetMapping("/{albumId}/letters")
    @Operation(summary = "편지 목록 조회", description = "특정 앨범의 모든 편지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<LetterListResponseDto>>> getLetters(@PathVariable Long albumId) {
        List<LetterListResponseDto> list = letterService.getLettersByAlbumIdWithPhotoCount(albumId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /** 편지 상세 조회. 없으면 ResourceNotFoundException → 404 */
    @GetMapping("/{albumId}/letters/{letterId}")
    @Operation(summary = "편지 상세 조회", description = "특정 앨범 내 특정 편지의 상세 정보를 조회합니다.")
    @ApiResponses(value = { @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "편지를 찾을 수 없습니다.") })
    public ResponseEntity<ApiResponse<LetterResponseDto>> getDetailLetter(
            @PathVariable Long albumId,
            @PathVariable Long letterId) {

        Letter letter = letterService.getLetterByIdAndAlbumId(letterId, albumId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 편지를 찾을 수 없습니다."));

        return ResponseEntity.ok(ApiResponse.success(new LetterResponseDto(letter)));
    }
}
