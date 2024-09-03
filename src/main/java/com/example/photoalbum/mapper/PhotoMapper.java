package com.example.photoalbum.mapper;

import com.example.photoalbum.domain.Album;
import com.example.photoalbum.domain.Photo;
import com.example.photoalbum.dto.AlbumDto;
import com.example.photoalbum.dto.PhotoDto;

public class PhotoMapper {
    public static PhotoDto convertToDto(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setFileSize(photo.getFileSize());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setAlbumId(photo.getAlbum().getAlbumId());
        photoDto.setThumbUrl(photo.getThumbUrl());
        return photoDto;
    }
}
