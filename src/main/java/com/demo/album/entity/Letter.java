package com.demo.album.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id", nullable = false, unique = true)
    private Long letterId;

    @Column(name = "letter_title", nullable = false)
    private String letterTitle; // 추가된 편지 제목 필드

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(nullable = false)
    private String letterColor;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    // 생성자
    public Letter(String letterTitle, String author, String content, boolean isAnonymous, String letterColor, Album album) {
        this.letterTitle = letterTitle;
        this.author = author;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.letterColor = letterColor;
        this.album = album;
        this.createdAt = LocalDateTime.now();
    }

    public Letter() {}

    public Long getLetterId() {
        return letterId;
    }

    public String getLetterTitle() {
        return letterTitle;
    }

    public void setLetterTitle(String letterTitle) {
        this.letterTitle = letterTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public String getLetterColor() {
        return letterColor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Album getAlbum() {
        return album;
    }
}
