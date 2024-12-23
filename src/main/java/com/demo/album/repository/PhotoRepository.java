package com.demo.album.repository;

import com.demo.album.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByLetter_LetterId(Long letterId);


    Optional<Photo> findByPhotoIdAndLetter_LetterId(Long photoId, Long letterId);
}
