package com.demo.album.controller;

import com.demo.album.service.StickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stickers")
public class StickerController {

    private final StickerService stickerService;

    @Autowired
    public StickerController(StickerService stickerService) {
        this.stickerService = stickerService;
    }

    @Operation(summary = "사용 가능한 스티커 목록 조회", description = "S3 버킷 내 저장된 스티커 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "스티커 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<String>> getAvailableStickers() {
        List<String> stickers = stickerService.getAvailableStickers();
        return ResponseEntity.ok(stickers);
    }
}
