package com.demo.album.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 편지 엔티티
 * - 한 앨범(Album)에 여러 편지가 속함 (N:1)
 * - 작성 시각은 DB에서 자동 설정(CreationTimestamp)
 */
@Entity
@Table(name = "letter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id", nullable = false, unique = true)
    private Long letterId;

    /** 편지 제목 */
    @Column(name = "letter_title", nullable = false)
    private String letterTitle;

    /** 작성자명 (익명이면 "익명" 등으로 표시) */
    @Column(nullable = false)
    private String author;

    /** 본문. TEXT 타입으로 긴 글 저장 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 익명 여부 */
    @Column(nullable = false)
    private boolean isAnonymous;

    /** 편지 배경/테두리 색상 */
    @Column(nullable = false)
    private String letterColor;

    /** 작성 시각. DB insert 시 자동 설정, 이후 수정 불가 */
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 소속 앨범 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;
}
