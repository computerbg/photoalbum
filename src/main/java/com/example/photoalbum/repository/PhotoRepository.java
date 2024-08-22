package com.example.photoalbum.repository;

import com.example.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
    int countByAlbum_AlbumId(Long AlbumId);
}
