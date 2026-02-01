package com.demo.album.dto;

import com.demo.album.entity.Letter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LetterListResponseDto {
    private Long letterId;
    private String letterTitle;
    private String author;
    private LocalDateTime createdAt;
    private long photoCount;

    public LetterListResponseDto(Letter letter) {
        this(letter, 0L);
    }

    public LetterListResponseDto(Letter letter, long photoCount) {
        this.letterId = letter.getLetterId();
        this.letterTitle = letter.getLetterTitle();
        this.author = letter.isAnonymous() ? "익명" : letter.getAuthor();
        this.createdAt = letter.getCreatedAt();
        this.photoCount = photoCount;
    }
}
