package com.example.photoalbum.repository;

import com.example.photoalbum.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album,Long> {

    int countByUser_UserId(String UserId); // 유저(ID)에 속해있는 구하기
    Optional<Album> findByAlbumName(String keyword);
    /*List<Album> findByAlbumNameContainingOrderByCreatedAtDesc(String keyword);
    List<Album> findByAlbumNameContainingOrderByCreatedAtAsc(String keyword);
    List<Album> findByAlbumNameContainingOrderByAlbumNameAsc(String keyword);
    List<Album> findByAlbumNameContainingOrderByAlbumNameDesc(String keyword);*/
    List<Album> findByUserUserIdAndAlbumNameContainingOrderByCreatedAtDesc(String userId,String keyword);
    List<Album> findByUserUserIdAndAlbumNameContainingOrderByCreatedAtAsc(String userId,String keyword);
    List<Album> findByUserUserIdAndAlbumNameContainingOrderByAlbumNameAsc(String userId,String keyword);
    List<Album> findByUserUserIdAndAlbumNameContainingOrderByAlbumNameDesc(String userId,String keyword);

}
