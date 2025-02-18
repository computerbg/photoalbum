package com.example.photoalbum.mapper;

import com.example.photoalbum.domain.Album;
import com.example.photoalbum.dto.AlbumDto;

import java.util.List;
import java.util.stream.Collectors;

public class AlbumMapper {
    public static AlbumDto convertToDto(Album album){
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumId(album.getAlbumId());
        albumDto.setAlbumName(album.getAlbumName());
        albumDto.setCreatedAt(album.getCreateAt());
        albumDto.setUserId(album.getUser().getUserId());
        return albumDto;
    }

    public static Album convertToModel(AlbumDto albumDto){
        Album album = new Album();
        album.setAlbumId(albumDto.getAlbumId());
        album.setAlbumName(albumDto.getAlbumName());
        album.setCreateAt(albumDto.getCreatedAt());
        return album;
    }

    public static List<AlbumDto> convertToDtoList(List<Album> albums)  {
        return albums.stream().map(AlbumMapper::convertToDto).collect(Collectors.toList());
    }

}
