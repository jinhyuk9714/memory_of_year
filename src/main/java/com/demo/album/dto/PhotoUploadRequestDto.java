package com.demo.album.dto;

import org.springframework.web.multipart.MultipartFile;

public class PhotoUploadRequestDto {

    private MultipartFile file;
    private String comment;
    private String stickerUrl;

    // 기본 생성자
    public PhotoUploadRequestDto() {}

    // Getter 및 Setter
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
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