package com.example.photoalbum.service;

import com.example.photoalbum.Constants;
import com.example.photoalbum.domain.Album;
import com.example.photoalbum.domain.Photo;
import com.example.photoalbum.domain.User;
import com.example.photoalbum.dto.AlbumDto;
import com.example.photoalbum.mapper.AlbumMapper;
import com.example.photoalbum.repository.AlbumRepository;
import com.example.photoalbum.repository.PhotoRepository;
import com.example.photoalbum.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    public AlbumDto getAlbumUseAlbumName(String albumName){// 앨범명으로 조회하기 8장 연습문제
        Optional<Album> res = albumRepository.findByAlbumName(albumName);
        if(res.isPresent()){
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            //albumDto.setCount(photoRepository.countByAlbum_AlbumId());
            return albumDto;
        }
        else{
            throw new EntityNotFoundException(String.format("앨범명 %s으로 조회되지 않았습니다.",albumName));
        }
    }
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

    public AlbumDto createAlbum(AlbumDto albumDto,String userId) throws IOException{
        Album album = AlbumMapper.convertToModel(albumDto);
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new EntityNotFoundException(String.format("No such user Id %s",userId));
        }
        User targetUser = user.get();
        album.setUser(targetUser);
        Album savedAlbum = this.albumRepository.save(album);
        this.createAlbumDirectories(savedAlbum);
        return AlbumMapper.convertToDto(album);
    }

    private  void createAlbumDirectories(Album album) throws IOException{
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX+"/photos/original/"+album.getUser().getUserId()+"/"+album.getAlbumId()));
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX+"/photos/thumb/"+album.getUser().getUserId()+"/"+album.getAlbumId()));
    }

    public List<AlbumDto> getAlbumList(String keyword, String sort,String orderBy){
        List<Album> albums;
        if(Objects.equals(sort,"byName")){
            if(Objects.equals(orderBy,"desc")){
                albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameDesc(keyword);
            }
            else if(Objects.equals(orderBy,"asc")){
                albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
            }
            else if(Objects.equals(orderBy,"")){
                albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
            }
            else{
                throw new IllegalArgumentException("알 수 없는 정렬 기준입니다");
            }
            //albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
        } else if (Objects.equals(sort,"byDate")) { //byDate가 디폴트이다 orderBy에 디폴트를 넣어줘야 하나 ?
            if(Objects.equals(orderBy,"desc")){
                albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
            }
            else if(Objects.equals(orderBy,"asc")){
                albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtAsc(keyword);
            }
            else if(Objects.equals(orderBy,"")){
                albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
            }
            else{
                throw new IllegalArgumentException("알 수 없는 정렬 기준입니다");
            }
            //albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다");
        }
        List<AlbumDto> albumDtos = AlbumMapper.convertToDtoList(albums);

        for (AlbumDto albumDto : albumDtos) { // 앨범 dto thumbnail 추가하는 코드
            List<Photo> top4 = photoRepository.findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(albumDto.getAlbumId()); // 앨범안에 있는 포토들중 최신 날짜 top4 뽑아 정렬
            albumDto.setThumbUrls(top4.stream().map(Photo::getThumbUrl).map(c->Constants.PATH_PREFIX+c).collect(Collectors.toList())); //어차피 사진만 보여주기 때문에 photo에 있는 url만 추출해서 쓰면 됨
        }

        return albumDtos;
    }

    //앨범 삭제
    public void deleteAlbum(Long AlbumId) throws IOException {
        Optional<Album> album = this.albumRepository.findById(AlbumId);
        if(album.isEmpty()){
            throw new NoSuchElementException(String.format("Album ID %d가 존재하지 않습니다",AlbumId));
        }
        Album deleteTargetAlbum = album.get();
        this.albumRepository.deleteById(deleteTargetAlbum.getAlbumId()); // 엔티티 삭제 cascade된 관련 앤티티도 다 삭제
        Files.delete(Paths.get(Constants.PATH_PREFIX+"/photos/original/"+deleteTargetAlbum.getUser().getUserId()+"/"+deleteTargetAlbum.getAlbumId()));
        Files.delete(Paths.get(Constants.PATH_PREFIX+"/photos/thumb/"+deleteTargetAlbum.getUser().getUserId()+"/"+deleteTargetAlbum.getAlbumId()));

    }

    public AlbumDto changeName(Long AlbumId, AlbumDto albumDto){
        Optional<Album> album = this.albumRepository.findById(AlbumId);
        if(album.isEmpty()){
            throw new NoSuchElementException(String.format("Album ID %d가 존재하지 않습니다",AlbumId));
        }
        Album updateAlbum = album.get();
        updateAlbum.setAlbumName(albumDto.getAlbumName());
        Album savedAlbum = this.albumRepository.save(updateAlbum); // 수정된 앨범 객체 저장 후 저장된 객체 반환
        return AlbumMapper.convertToDto(savedAlbum); // 앨범객체 DTO로 변환
    }

}
