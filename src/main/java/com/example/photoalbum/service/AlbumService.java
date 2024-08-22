package com.example.photoalbum.service;

import com.example.photoalbum.Constants;
import com.example.photoalbum.domain.Album;
import com.example.photoalbum.dto.AlbumDto;
import com.example.photoalbum.mapper.AlbumMapper;
import com.example.photoalbum.repository.AlbumRepository;
import com.example.photoalbum.repository.PhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    public AlbumDto getAlbum(Long albumId){
        Optional<Album> res = albumRepository.findById(albumId);
        if(res.isPresent()){
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumId));
            return albumDto;
        }else{
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다",albumId));
        }
    }

    public AlbumDto createAlbum(AlbumDto albumDto) throws IOException{
        Album album = AlbumMapper.convertToModel(albumDto);
        this.albumRepository.save(album);
        this.createAlbumDirectories(album);
        return AlbumMapper.convertToDto(album);
    }

    private  void createAlbumDirectories(Album album) throws IOException{
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX+"/photos/original/"+album.getAlbumId()));
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX+"/photos/thumb/"+album.getAlbumId()));
    }


}
