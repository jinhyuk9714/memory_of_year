package com.demo.album.controller;

import com.demo.album.dto.ApiResponse;
import com.demo.album.service.StickerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stickers")
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;

    @GetMapping
    @Operation(summary = "사용 가능한 스티커 목록 조회", description = "S3 버킷 내 저장된 스티커 목록을 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스티커 목록 조회 성공")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableStickers() {
        List<String> stickers = stickerService.getAvailableStickers();
        return ResponseEntity.ok(ApiResponse.success(stickers));
    }
}
