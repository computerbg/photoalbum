package com.example.photoalbum.controller;

import com.example.photoalbum.domain.Album;
import com.example.photoalbum.dto.AlbumDto;
import com.example.photoalbum.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController // 해당 클래스가 spring에서 관리하는 컨트롤러임을 나타내고 Rest API를 사용할 것임을 나타냄
@RequestMapping("/albums") // 해당 컨트롤러가 처리할 URL 경로의 앞부분을 나타냄 위에 파일에서는 https://<url>/albums 으로 들어오는 모든 요청은 해당 컨트롤러의 메서드에서 처리하게 됩니다.
@RequiredArgsConstructor
public class AlbumController {


//    @Autowired
//    AlbumService albumService;
    private final  AlbumService albumService; // 생성자 주입과 필드 주입 지금 당장은 new가 붙어 있으면 필드주입으로 보면 될 거 같다.


    @RequestMapping(value = "/{albumId}",method = RequestMethod.GET) // 요청
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("albumId") final long albumId){ // 요청

        AlbumDto album = albumService.getAlbum(albumId);  // 실제 우리가 짜는 코드 라인. 서비스에게 위임 .

        return new ResponseEntity<>(album, HttpStatus.OK);  // 응답

    }

    @RequestMapping(value = "/query",method = RequestMethod.GET)
    public ResponseEntity<AlbumDto> getAlbumByQuery(@RequestParam(value = "albumId")final Long albumId){
        AlbumDto albumDto = albumService.getAlbum(albumId);
        return new ResponseEntity<>(albumDto,HttpStatus.OK);
    }

    @RequestMapping(value = "/json_body",method = RequestMethod.POST)
    public ResponseEntity<AlbumDto> getAlbumByJson(@RequestBody final AlbumDto albumDto){
        AlbumDto album = albumService.getAlbum(albumDto.getAlbumId());
        return new ResponseEntity<>(album,HttpStatus.OK);
    }

    @RequestMapping(value="",method = RequestMethod.POST)
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody final AlbumDto albumDto) throws IOException {
        AlbumDto savedAlbumDto = albumService.createAlbum(albumDto);
        return new ResponseEntity<>(savedAlbumDto,HttpStatus.OK);

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<AlbumDto>>
    getAlbumList(@RequestParam(value = "keyword", required = false,defaultValue = "") final String keyword,
                 @RequestParam(value = "sort",required = false,defaultValue = "byDate") final String sort,
                 @RequestParam(value = "orderBy",required = false,defaultValue = "")final  String orderBy){
        List<AlbumDto> albumDtos = albumService.getAlbumList(keyword,sort,orderBy);
        return new ResponseEntity<>(albumDtos,HttpStatus.OK);
    }

    //앨범 삭제
    @RequestMapping(value = "/{albumId}",method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAlbum(@PathVariable("albumId") final long albumId) throws IOException {
        albumService.deleteAlbum(albumId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{albumId}",method = RequestMethod.PUT)
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable("albumId") final long albumId,
                                                @RequestBody final AlbumDto albumDto){
        AlbumDto res = albumService.changeName(albumId,albumDto);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

}
