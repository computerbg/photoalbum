package com.example.photoalbum.service;

import com.example.photoalbum.Constants;
import com.example.photoalbum.domain.Album;
import com.example.photoalbum.domain.Photo;
import com.example.photoalbum.dto.AlbumDto;
import com.example.photoalbum.mapper.AlbumMapper;
import com.example.photoalbum.repository.AlbumRepository;
import com.example.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    AlbumService albumService;


    @Test
    void getAlbumUseAlbumName(){ // 8/26일 여기까지 photoController랑 service 주석처리 했음 원할한 테스트를 위해
        Album album = new Album();
        album.setAlbumName("테스트1");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto resAlbum = albumService.getAlbumUseAlbumName(savedAlbum.getAlbumName());
        assertEquals("테스트1",resAlbum.getAlbumName());
    }

    @Test
    @DisplayName("앨범 가져오기")
    void getAlbum() throws InterruptedException {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals("테스트",resAlbum.getAlbumName());
    }

    @Test
    void testPhotoCount(){
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        //사진을 생성하고, setAlbum을 통해 앨범을 지정해준 이후, repository에 사진을 저장한다
        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(savedAlbum);
        photoRepository.save(photo1);

        Photo photo2 = new Photo();
        photo2.setFileName("사진2");
        photo2.setAlbum(savedAlbum);
        photoRepository.save(photo2);

        AlbumDto albumDto = AlbumMapper.convertToDto(savedAlbum);
        albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
        assertEquals(2,albumDto.getCount());
    }

  /*  @Test
    @DisplayName("앨범 생성하기 및 바로 폴더 삭제")
    void testAlbumCreate() throws IOException {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("테스트");
        AlbumDto savedAlbumDto = albumService.createAlbum(albumDto);
        assertEquals("테스트",savedAlbumDto.getAlbumName());
        Files.delete(Paths.get(Constants.PATH_PREFIX+"/photos/original/"+savedAlbumDto.getAlbumId()));
        Files.delete(Paths.get(Constants.PATH_PREFIX+"/photos/thumb/"+savedAlbumDto.getAlbumId()));
    }*/



    @Test
    @DisplayName("앨범 목록")
    void testAlbumRepository() throws InterruptedException {
        Album album1 = new Album();
        Album album2 = new Album();
        album1.setAlbumName("aaaa");
        album2.setAlbumName("aaab");

        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(1); //시간차를 벌리기위해 두번째 앨범 생성 1초 딜레이
        albumRepository.save(album2);

        //최신순 정렬, 두번째로 생성한 앨범이 먼저 나와야합니다
        List<Album> resDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("aaa");
        assertEquals("aaab", resDate.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaab 인지 체크
        assertEquals("aaaa", resDate.get(1).getAlbumName()); // 1번째 Index가 첫번째 앨범명 aaaa 인지 체크
        assertEquals(2, resDate.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크

        //앨범명 정렬, aaaa -> aaab 기준으로 나와야합니다
        List<Album> resName = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc("aaa");
        assertEquals("aaaa", resName.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaaa 인지 체크
        assertEquals("aaab", resName.get(1).getAlbumName()); // 1번째 Index가 두번째 앨범명 aaab 인지 체크
        assertEquals(2, resName.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크
    }

   /* @Test
    @DisplayName("앨범 삭제")
    void testDeleteAlbum() throws IOException {
        //앨범 생성 후 삭제랑
        //앨범 생성 후 사진넣고 삭제
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("삭제할거임");
        AlbumDto res = albumService.createAlbum(albumDto);

        AlbumMapper albumMapper = new AlbumMapper();
        Album album =albumMapper.convertToModel(res);

        Long albumId = res.getAlbumId();
        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(album);
        photoRepository.save(photo1);

        Photo photo2 = new Photo();
        photo2.setFileName("사진2");
        photo2.setAlbum(album);

        photoRepository.save(photo2); // 사진이 있을 때에도 정상 작동하는지 평가
        albumService.deleteAlbum(albumId);


    }

    @Test
    @DisplayName("앨범 바꾸기")
    void testChangeAlbumName() throws IOException {
        //앨범 생성
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("변경전");
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId = res.getAlbumId(); // 생성된 앨범 아이디 추출
        AlbumDto updateDto = new AlbumDto();
        updateDto.setAlbumName("변경후"); // 업데이트용 Dto 생성
        albumService.changeName(albumId, updateDto);

        AlbumDto updatedDto = albumService.getAlbum(albumId);

        //앨범명 변경되었는지 확인
        assertEquals("변경후", updatedDto.getAlbumName());
    }*/
}