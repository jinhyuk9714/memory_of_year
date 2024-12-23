package com.demo.album.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetterRequestDto {
    private String letterTitle; // 추가된 편지 제목
    private String author;
    private String content;
    private boolean anonymous;
    private String letterColor;
}
