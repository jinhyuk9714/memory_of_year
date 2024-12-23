package com.demo.album.controller;

import com.demo.album.dto.LetterRequestDto;
import com.demo.album.dto.LetterListResponseDto;
import com.demo.album.dto.LetterResponseDto;
import com.demo.album.entity.Letter;
import com.demo.album.service.LetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
public class LetterController {

    private final LetterService letterService;

    public LetterController(LetterService letterService) {
        this.letterService = letterService;
    }

    @PostMapping("/{albumId}/create")
    @Operation(summary = "편지 생성", description = "특정 앨범에 새 편지를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "편지 생성 성공"),
            @ApiResponse(responseCode = "404", description = "앨범을 찾을 수 없습니다.")
    })
    public ResponseEntity<?> createLetter(
            @PathVariable Long albumId,
            @RequestBody LetterRequestDto letterRequest) {

        try {
            Letter letter = letterService.createLetter(albumId, letterRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new LetterListResponseDto(letter));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("앨범을 찾을 수 없습니다.");
        }
    }

    @GetMapping("/{albumId}/letters")
    @Operation(summary = "편지 목록 조회", description = "특정 앨범의 모든 편지를 조회합니다.")
    public ResponseEntity<List<LetterListResponseDto>> getLetters(@PathVariable Long albumId) {
        List<Letter> letters = letterService.getLettersByAlbumId(albumId);
        List<LetterListResponseDto> response = letters.stream()
                .map(LetterListResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{albumId}/letters/{letterId}")
    @Operation(summary = "편지 상세 조회", description = "특정 앨범 내 특정 편지의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getDetailLetter(@PathVariable Long albumId, @PathVariable Long letterId) {
        Optional<Letter> letterOptional = letterService.getLetterByIdAndAlbumId(letterId, albumId);

        if (letterOptional.isPresent()) {
            return ResponseEntity.ok(new LetterResponseDto(letterOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 편지를 찾을 수 없습니다.");
        }
    }
}
