package com.demo.album.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 앨범 엔티티
 * - 한 명의 User(owner)가 여러 앨범을 가질 수 있음 (N:1)
 * - 한 앨범에 여러 Letter가 속함 (1:N, cascade로 함께 삭제)
 */
@Entity
@Table(name = "album")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long albumId;

    /** 앨범 제목 */
    @Column(nullable = false)
    private String title;

    /** 앨범 대표 색상 (예: #FF5733) */
    private String albumColor;

    /** 공개(true) / 비공개(false) */
    @Column(nullable = false)
    private boolean visibility;

    /** 대표 스티커 이미지 URL (S3 등) */
    @Column(name = "sticker_url")
    private String stickerUrl;

    /** 소유자. LAZY로 필요할 때만 조회 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    /** 앨범에 속한 편지 목록. 앨범 삭제 시 편지도 삭제(orphanRemoval) */
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Letter> letters = new ArrayList<>();
}
