package com.example.photoalbum.service;

import com.example.photoalbum.Constants;
import com.example.photoalbum.domain.Album;
import com.example.photoalbum.domain.Photo;
import com.example.photoalbum.dto.ChangePhotoDto;
import com.example.photoalbum.dto.PhotoDto;
import com.example.photoalbum.mapper.PhotoMapper;
import com.example.photoalbum.repository.AlbumRepository;
import com.example.photoalbum.repository.PhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";

    public PhotoDto getPhoto(Long albumId,Long photoId){
        Optional<Album> res = albumRepository.findById(albumId);

        if(res.isPresent()){
            Optional<Photo> photoRes = photoRepository.findByPhotoIdAndAlbum_AlbumId(photoId,albumId);
            if(photoRes.isPresent()){
                PhotoDto photoDto = PhotoMapper.convertToDto(photoRes.get());
                return photoDto;
            }
            else{
                throw new EntityNotFoundException(String.format("사진 아이디 %d로 조회되지 않았습니다.",photoId));
            }
        }else{
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다",albumId));
        }
    }

    public List<PhotoDto> getPhotos(Long albumId){
        Optional<List<Photo>> res = photoRepository.findByAlbum_AlbumId(albumId);
        if(res.isEmpty()){
            throw new EntityNotFoundException("앨범이 존재하지 않습니다.");
        }
        List<Photo> photos = res.get();
        List<PhotoDto> photoDtos = new ArrayList<>();
        for (Photo photo : photos) {
            photoDtos.add(PhotoMapper.convertToDto(photo));
        }
        return photoDtos;
    }

    public PhotoDto savePhoto(MultipartFile file, Long albumId) throws IOException {

        Optional<Album> res = albumRepository.findById(albumId);
        if(res.isEmpty()){
            throw new EntityNotFoundException("앨범이 존재하지 않습니다.");
        }
        String fileName =file.getOriginalFilename();
        int fileSize = (int)file.getSize();
        fileName = getNextFileName(fileName,albumId);
        saveFile(file,res.get().getUser().getUserId(),albumId,fileName);

        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/"+res.get().getUser().getUserId()+"/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/"+res.get().getUser().getUserId()+"/"+albumId + "/" + fileName);
        photo.setFileName(fileName);
        photo.setFileSize(fileSize);
        photo.setAlbum(res.get());
        Photo createdPhoto = photoRepository.save(photo);
        return PhotoMapper.convertToDto(createdPhoto); // convertToDto 구현해야함
    }

    private String getNextFileName(String fileName,Long albumId){
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);

        Optional<Photo> res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName,albumId);

        int count = 2;
        while(res.isPresent()){
            fileName = String.format("%s (%d).%s",fileNameNoExt,count,ext);
            res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName,albumId);
            count++;
        }
        return fileName;
    }

    private void saveFile(MultipartFile file,String userId ,Long AlbumId, String fileName) throws IOException {
        try {
            String filePath = userId+"/"+AlbumId + "/" + fileName;
            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));

            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            if (ext == null) {
                throw new IllegalArgumentException("No Extension");
            }
            ImageIO.write(thumbImg, ext, thumbFile); // 원본파일은 file.copy 쓰는데 왜 썸네일은 ImageIo.write를 썻을까
        }catch (Exception e){
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public File getImageFile(Long photoId){
        Optional<Photo> res = photoRepository.findById(photoId);
        if(res.isEmpty()){
            throw new EntityNotFoundException(String.format("사진 ID %d를 찾을 수 없습니다.",photoId));
        }
        return new File(Constants.PATH_PREFIX + res.get().getOriginalUrl());
    }

    public void movePhoto(ChangePhotoDto changePhotoDto) {
        List<Long> photoIds = changePhotoDto.getPhotoIds();
        Optional<Album> toAlbum = albumRepository.findById(changePhotoDto.getToAlbumId());
        if (toAlbum.isEmpty()) {
            throw new EntityNotFoundException(String.format("사진 ID %d를 찾을 수 없습니다.", changePhotoDto.getToAlbumId()));
        }
        Album album = toAlbum.get();
        for (Long photoId : photoIds) {
            Optional<Photo> byId = photoRepository.findById(photoId);
            if(byId.isEmpty()){
                throw new EntityNotFoundException(String.format("사진 ID %d를 찾을 수 없습니다.",photoId));
            }
            Photo photo = byId.get(); // 앤티티를 가져온 후 수정 후 다시 저장
            photo.setAlbum(album);
            photoRepository.save(photo);
        }
    }

    public void deletePhoto(List<Long> photoIds){
        for (Long photoId : photoIds) {
            Optional<Photo> byId = photoRepository.findById(photoId);
            if(byId.isEmpty()){
                throw new EntityNotFoundException(String.format("사진 ID %d를 찾을 수 없습니다.",photoId));
            }
            Photo photo = byId.get();
            photoRepository.deleteById(photo.getPhotoId());
        }
    }
}
