package com.example.photoalbum.repository;

import com.example.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
    int countByAlbum_AlbumId(Long AlbumId);

    Optional<List<Photo>> findByAlbum_AlbumId(Long albumId);

    Optional<Photo> findByPhotoIdAndAlbum_AlbumId(Long photoId,Long albumId); // 포토아이디만 있어도된다
    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long AlbumId);

    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName,Long albumId);
}
