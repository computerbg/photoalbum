package com.example.photoalbum.controller;

import com.example.photoalbum.domain.Album;
import com.example.photoalbum.dto.AlbumDto;
import com.example.photoalbum.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController // 해당 클래스가 spring에서 관리하는 컨트롤러임을 나타내고 Rest API를 사용할 것임을 나타냄
@RequestMapping("/albums") // 해당 컨트롤러가 처리할 URL 경로의 앞부분을 나타냄 위에 파일에서는 https://<url>/albums 으로 들어오는 모든 요청은 해당 컨트롤러의 메서드에서 처리하게 됩니다.
public class AlbumController {


    @Autowired
    AlbumService albumService;

    @RequestMapping(value = "/{albumId}",method = RequestMethod.GET)
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("albumId") final long albumId){
        AlbumDto album = albumService.getAlbum(albumId);
        return new ResponseEntity<>(album, HttpStatus.OK);

    }

    @RequestMapping(value="",method = RequestMethod.POST)
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody final AlbumDto albumDto) throws IOException {
        AlbumDto savedAlbumDto = albumService.createAlbum(albumDto);
        return new ResponseEntity<>(savedAlbumDto,HttpStatus.OK);

    }


}
