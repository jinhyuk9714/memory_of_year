package com.demo.album.service;

import com.demo.album.entity.Letter;
import com.demo.album.entity.Photo;
import com.demo.album.repository.LetterRepository;
import com.demo.album.repository.PhotoRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final LetterRepository letterRepository;
    private final S3Service s3Service;
    private Logger logger;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, LetterRepository letterRepository, S3Service s3Service) {
        this.photoRepository = photoRepository;
        this.letterRepository = letterRepository;
        this.s3Service = s3Service;
    }

    public Photo addPhoto(Long letterId, MultipartFile file, String comment, String stickerUrl) throws IOException {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new IllegalArgumentException("편지를 찾을 수 없습니다."));

        // S3Service를 사용하여 파일 업로드
        String fileUrl;
        try {
            fileUrl = s3Service.uploadFile(file);
        } catch (Exception e) {
            logger.error("파일 업로드 중 오류 발생: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류 발생");
        }

        // Photo 엔티티 생성 및 저장
        Photo photo = new Photo(fileUrl, comment, stickerUrl, letter);
        return photoRepository.save(photo);
    }



    public List<Photo> getPhotosByLetterId(Long letterId) {
        return photoRepository.findByLetter_LetterId(letterId);
    }

    public Optional<Photo> getPhotoByIdAndLetterId(Long photoId, Long letterId) {
        return photoRepository.findByPhotoIdAndLetter_LetterId(photoId, letterId);
    }
}
