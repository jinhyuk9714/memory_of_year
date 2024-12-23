package com.demo.album.entity;

import jakarta.persistence.*;

@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @Column(nullable = false)
    private String url;

    private String comment;

    private String stickerUrl; // 스티커 URL 저장

    @ManyToOne
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;

    public Photo(String url, String comment, String stickerUrl, Letter letter) {
        this.url = url;
        this.comment = comment;
        this.stickerUrl = stickerUrl;
        this.letter = letter;
    }

    // 기본 생성자 필요
    public Photo() {}

    // Getters and Setters
    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
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

    public Letter getLetter() {
        return letter;
    }

    public void setLetter(Letter letter) {
        this.letter = letter;
    }
}
