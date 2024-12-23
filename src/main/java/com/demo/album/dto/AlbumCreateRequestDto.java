package com.demo.album.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앨범 생성 요청")
public class AlbumCreateRequestDto {
    @Schema(description = "앨범 제목", example = "나만의 앨범")
    private String title;

    @Schema(description = "앨범 색상", example = "#FF5733")
    private String albumColor;

    @Schema(description = "앨범 공개 여부", example = "true")
    private boolean visibility;

    @Schema(description = "스티커 URL", example = "https://memory-of-year-bucket.s3.ap-southeast-2.amazonaws.com/stickers/ball.png")
    private String stickerUrl; // 선택한 스티커의 URL 추가

    // 기본 생성자
    public AlbumCreateRequestDto() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumColor() {
        return albumColor;
    }

    public void setAlbumColor(String albumColor) {
        this.albumColor = albumColor;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }
}
