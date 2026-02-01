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

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;

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

    @GetMapping("/{albumId}/letters")
    @Operation(summary = "편지 목록 조회", description = "특정 앨범의 모든 편지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<LetterListResponseDto>>> getLetters(@PathVariable Long albumId) {
        List<LetterListResponseDto> list = letterService.getLettersByAlbumId(albumId).stream()
                .map(LetterListResponseDto::new)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

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
