package com.demo.album.dto;

import com.demo.album.entity.Letter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LetterListResponseDto {
    private Long letterId;
    private String letterTitle; // 추가된 편지 제목
    private String author;
    private LocalDateTime createdAt;

    public LetterListResponseDto(Letter letter) {
        this.letterId = letter.getLetterId();
        this.letterTitle = letter.getLetterTitle();
        this.author = letter.isAnonymous() ? "익명" : letter.getAuthor();
        this.createdAt = letter.getCreatedAt();
    }
}
