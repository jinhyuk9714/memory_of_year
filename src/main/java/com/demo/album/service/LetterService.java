package com.demo.album.service;

import com.demo.album.dto.LetterRequestDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.Letter;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.AlbumRepository;
import com.demo.album.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 편지 비즈니스 로직
 * - 앨범에 편지 생성, 앨범별 편지 목록/상세 조회
 */
@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final AlbumRepository albumRepository;

    /** 앨범에 편지 생성. 앨범 없으면 ResourceNotFoundException */
    public Letter createLetter(Long albumId, LetterRequestDto letterRequest) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("앨범", albumId));

        Letter letter = Letter.builder()
                .letterTitle(letterRequest.getLetterTitle())
                .author(letterRequest.getAuthor())
                .content(letterRequest.getContent())
                .isAnonymous(letterRequest.isAnonymous())
                .letterColor(letterRequest.getLetterColor())
                .album(album)
                .build();

        return letterRepository.save(letter);
    }

    /** 앨범 ID로 편지 목록 조회 */
    public List<Letter> getLettersByAlbumId(Long albumId) {
        return letterRepository.findByAlbumAlbumId(albumId);
    }

    /** 편지 ID + 앨범 ID로 편지 조회 (권한 검증용) */
    public Optional<Letter> getLetterByIdAndAlbumId(Long letterId, Long albumId) {
        return letterRepository.findByLetterIdAndAlbumAlbumId(letterId, albumId);
    }
}
