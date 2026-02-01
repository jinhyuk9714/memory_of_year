package com.demo.album.service;

import com.demo.album.entity.Letter;
import com.demo.album.entity.Photo;
import com.demo.album.exception.ResourceNotFoundException;
import com.demo.album.repository.LetterRepository;
import com.demo.album.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 사진 비즈니스 로직
 * - 편지에 사진 추가(S3 업로드 후 Photo 저장), 편지별 사진 목록/상세 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final LetterRepository letterRepository;
    private final S3Service s3Service;

    /** 편지에 사진 추가: S3 업로드 후 Photo 엔티티 저장. 편지 없으면 ResourceNotFoundException */
    public Photo addPhoto(Long letterId, MultipartFile file, String comment, String stickerUrl) throws IOException {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new ResourceNotFoundException("편지", letterId));

        String fileUrl = s3Service.uploadFile(file);

        Photo photo = Photo.builder()
                .url(fileUrl)
                .comment(comment)
                .stickerUrl(stickerUrl)
                .letter(letter)
                .build();

        return photoRepository.save(photo);
    }

    /** 편지 ID로 사진 목록 조회 */
    public List<Photo> getPhotosByLetterId(Long letterId) {
        return photoRepository.findByLetter_LetterId(letterId);
    }

    /** 사진 ID + 편지 ID로 사진 조회 (권한 검증용) */
    public Optional<Photo> getPhotoByIdAndLetterId(Long photoId, Long letterId) {
        return photoRepository.findByPhotoIdAndLetter_LetterId(photoId, letterId);
    }
}
