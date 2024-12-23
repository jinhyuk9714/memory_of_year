package com.demo.album.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사진 업로드 요청을 위한 DTO")
public class PhotoRequestDto {

    @Schema(description = "사진의 URL", example = "http://example.com/photo.jpg")
    private String url;

    @Schema(description = "사진에 대한 코멘트", example = "This is a sample photo comment")
    private String comment;

    private String stickerUrl;

    public PhotoRequestDto() {}

    public PhotoRequestDto(String url, String comment) {
        this.url = url;
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }
}
