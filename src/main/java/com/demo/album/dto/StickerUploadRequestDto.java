package com.demo.album.dto;

import org.springframework.web.multipart.MultipartFile;

public class StickerUploadRequestDto {

    private MultipartFile file;

    public StickerUploadRequestDto(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
