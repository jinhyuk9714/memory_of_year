package com.demo.album.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사진 엔티티
 * - 한 편지(Letter)에 여러 사진이 속함 (N:1)
 * - url은 S3 등에 업로드된 이미지 URL
 */
@Entity
@Table(name = "photo", indexes = {
        @Index(name = "idx_photo_letter_id", columnList = "letter_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    /** 이미지 URL (S3 등) */
    @Column(nullable = false)
    private String url;

    /** 사진에 대한 코멘트 */
    private String comment;

    /** 사진에 붙인 스티커 이미지 URL */
    @Column(name = "sticker_url")
    private String stickerUrl;

    /** 소속 편지 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;
}
