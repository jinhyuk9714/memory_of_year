package com.demo.album.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사진 조회 응답을 위한 DTO")
public class PhotoResponseDto {

    @Schema(description = "사진 ID", example = "1")
    private Long photoId;

    @Schema(description = "사진의 URL", example = "http://example.com/photo.jpg")
    private String url;

    @Schema(description = "사진에 대한 코멘트", example = "This is a sample photo comment")
    private String comment;

    @Schema(description = "사진에 적용된 스티커 URL", example = "https://example.com/sticker.png")
    private String stickerUrl;

    public PhotoResponseDto(Long photoId, String url, String comment, String stickerUrl) {
        this.photoId = photoId;
        this.url = url;
        this.comment = comment;
        this.stickerUrl = stickerUrl;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public String getUrl() {
        return url;
    }

    public String getComment() {
        return comment;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }
}
