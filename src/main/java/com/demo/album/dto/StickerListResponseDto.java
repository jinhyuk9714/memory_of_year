package com.demo.album.dto;

import java.util.List;

public class StickerListResponseDto {

    private List<StickerResponseDto> stickers;

    public StickerListResponseDto(List<StickerResponseDto> stickers) {
        this.stickers = stickers;
    }

    // Getters and Setters
    public List<StickerResponseDto> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerResponseDto> stickers) {
        this.stickers = stickers;
    }
}
