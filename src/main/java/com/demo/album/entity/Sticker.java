package com.demo.album.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 스티커 마스터 엔티티
 * - 앨범/사진에서 사용하는 스티커는 URL 문자열로만 저장하고,
 *   실제 목록은 S3 폴더(stickers/) 기반으로도 제공 가능
 */
@Entity
@Table(name = "sticker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** S3 등에 저장된 파일명 (중복 불가) */
    @Column(nullable = false, unique = true)
    private String fileName;

    /** 스티커 이미지 전체 URL */
    @Column(nullable = false)
    private String fileUrl;

    /** 등록 시각. 자동 설정 */
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
