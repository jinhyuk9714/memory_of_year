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

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final LetterRepository letterRepository;
    private final S3Service s3Service;

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

    public List<Photo> getPhotosByLetterId(Long letterId) {
        return photoRepository.findByLetter_LetterId(letterId);
    }

    public Optional<Photo> getPhotoByIdAndLetterId(Long photoId, Long letterId) {
        return photoRepository.findByPhotoIdAndLetter_LetterId(photoId, letterId);
    }
}
