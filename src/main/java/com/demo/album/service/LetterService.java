package com.demo.album.service;

import com.demo.album.dto.LetterListResponseDto;
import com.demo.album.dto.LetterRequestDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.Letter;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.AlbumRepository;
import com.demo.album.repository.LetterRepository;
import com.demo.album.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final PhotoRepository photoRepository;

    @Value("${app.performance.use-n1-letters:false}")
    private boolean useN1Letters;

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

    /** 앨범 ID로 편지 목록 조회 (photoCount 포함) */
    public List<LetterListResponseDto> getLettersByAlbumIdWithPhotoCount(Long albumId) {
        if (useN1Letters) {
            return getLettersN1(albumId);
        }
        return getLettersOptimized(albumId);
    }

    /** N+1 버전: 편지 1회 + 편지별 사진 count N회 = 1+N 쿼리 */
    private List<LetterListResponseDto> getLettersN1(Long albumId) {
        List<Letter> letters = letterRepository.findByAlbumAlbumId(albumId);
        return letters.stream()
                .map(l -> new LetterListResponseDto(l, photoRepository.countByLetter_LetterId(l.getLetterId())))
                .toList();
    }

    /** 최적화 버전: 편지+사진개수 1회 쿼리 */
    private List<LetterListResponseDto> getLettersOptimized(Long albumId) {
        List<Object[]> rows = letterRepository.findLettersWithPhotoCountByAlbumId(albumId);
        return rows.stream()
                .map(row -> new LetterListResponseDto((Letter) row[0], (Long) row[1]))
                .toList();
    }

    /** 앨범 ID로 편지 목록 조회 (내부용, photoCount 없음) */
    public List<Letter> getLettersByAlbumId(Long albumId) {
        return letterRepository.findByAlbumAlbumId(albumId);
    }

    /** 편지 ID + 앨범 ID로 편지 조회 (권한 검증용) */
    public Optional<Letter> getLetterByIdAndAlbumId(Long letterId, Long albumId) {
        return letterRepository.findByLetterIdAndAlbumAlbumId(letterId, albumId);
    }
}
