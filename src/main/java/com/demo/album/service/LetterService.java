package com.demo.album.service;

import com.demo.album.dto.LetterRequestDto;
import com.demo.album.entity.Album;
import com.demo.album.entity.Letter;
import com.demo.album.repository.AlbumRepository;
import com.demo.album.repository.LetterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LetterService {

    private final LetterRepository letterRepository;
    private final AlbumRepository albumRepository;

    public LetterService(LetterRepository letterRepository, AlbumRepository albumRepository) {
        this.letterRepository = letterRepository;
        this.albumRepository = albumRepository;
    }

    public Letter createLetter(Long albumId, LetterRequestDto letterRequest) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        Letter letter = new Letter(
                letterRequest.getLetterTitle(),
                letterRequest.getAuthor(),
                letterRequest.getContent(),
                letterRequest.isAnonymous(),
                letterRequest.getLetterColor(),
                album
        );

        return letterRepository.save(letter);
    }

    public List<Letter> getLettersByAlbumId(Long albumId) {
        return letterRepository.findByAlbumAlbumId(albumId);
    }

    public Optional<Letter> getLetterByIdAndAlbumId(Long letterId, Long albumId) {
        return letterRepository.findByLetterIdAndAlbumAlbumId(letterId, albumId);
    }
}
