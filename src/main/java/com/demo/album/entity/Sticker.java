package com.demo.album.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Sticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileName; // S3에 저장된 파일 이름

    @Column(nullable = false)
    private String fileUrl; // S3 파일 URL

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
