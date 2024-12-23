package com.demo.album.dto;

import com.demo.album.entity.Letter;

import java.time.LocalDateTime;

public class LetterResponseDto {
    private Long letterId;
    private String letterTitle; // 추가된 편지 제목
    private String content;
    private String letterColor;
    private String author;
    private boolean anonymous;
    private LocalDateTime createdAt;

    public LetterResponseDto(Letter letter) {
        this.letterId = letter.getLetterId();
        this.letterTitle = letter.getLetterTitle();
        this.content = letter.getContent();
        this.letterColor = letter.getLetterColor();
        this.author = letter.isAnonymous() ? "익명" : letter.getAuthor();
        this.anonymous = letter.isAnonymous();
        this.createdAt = letter.getCreatedAt();
    }

    public Long getLetterId() {
        return letterId;
    }

    public String getLetterTitle() {
        return letterTitle;
    }

    public String getContent() {
        return content;
    }

    public String getLetterColor() {
        return letterColor;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
