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

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final AlbumRepository albumRepository;

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

    public List<Letter> getLettersByAlbumId(Long albumId) {
        return letterRepository.findByAlbumAlbumId(albumId);
    }

    public Optional<Letter> getLetterByIdAndAlbumId(Long letterId, Long albumId) {
        return letterRepository.findByLetterIdAndAlbumAlbumId(letterId, albumId);
    }
}
