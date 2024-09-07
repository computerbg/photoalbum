package com.example.photoalbum.controller;

import com.example.photoalbum.dto.ChangePhotoDto;
import com.example.photoalbum.dto.PhotoDto;
import com.example.photoalbum.service.PhotoService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/back_end/{userId}/albums/{albumId}/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @RequestMapping(value="/{photoId}")
    public ResponseEntity<PhotoDto> getPhoto(@PathVariable("albumId") final long albumId,
                                             @PathVariable("photoId") final long photoId){
        PhotoDto photoDto = photoService.getPhoto(albumId,photoId);
        return new ResponseEntity<>(photoDto,HttpStatus.OK);
    }

    //사진 업로드
    @RequestMapping(value = "",method = RequestMethod.POST)
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final Long albumId,
                                                       @RequestParam("photos")MultipartFile[] files) throws IOException {
        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files){
            if(file.getContentType().startsWith("image")==false){ // 실제 이미지 파일인지 확인 하는 코드 근데 이건 service가 할일 아닌가?
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            PhotoDto photoDto = photoService.savePhoto(file,albumId);
            photos.add(photoDto);
        }
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response){
        try{
            if(photoIds.length == 1){
                File file = photoService.getImageFile(photoIds[0]);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(file), outputStream);
                outputStream.close();
            }
            else{ //zip 파일 코드 구현
                List<File> files = new ArrayList<>();
                for (Long photoId : photoIds) {
                    File file = photoService.getImageFile(photoId);
                    files.add(file);
                }
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition","attachment; filename=files.zip");

                try(ServletOutputStream outputStream = response.getOutputStream();
                    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)){
                    for (File file : files) {
                        try(FileInputStream fis = new FileInputStream(file)){
                            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

                            IOUtils.copy(fis,zipOutputStream);

                            zipOutputStream.closeEntry();
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //사진 목록 불러오기
    @RequestMapping(value = "" , method = RequestMethod.GET)
    public ResponseEntity<List<PhotoDto>> getPhotos(@PathVariable("albumId") final long albumId,
                                                    @RequestParam(value = "sort",required = false,defaultValue = "byDate") final String sort,
                                                    @RequestParam(value = "keyword",required = false,defaultValue = "")final  String keyword){
        List<PhotoDto> photos = photoService.getPhotos(albumId);
        return new ResponseEntity<>(photos,HttpStatus.OK);
    }

    //사진 앨범 옮기기
    @RequestMapping(value = "/move", method = RequestMethod.PUT)
    public ResponseEntity<List<PhotoDto>> movePhoto(@PathVariable("albumId") final long albumId,
                                                    @RequestBody final ChangePhotoDto changePhotoDto) {

        photoService.movePhoto(changePhotoDto);//포토서비스에게 옮겨라고 메시지 보내기
        List<PhotoDto> photos = photoService.getPhotos(changePhotoDto.getFromAlbumId());//포토서비스에게 사진목록 조회 메시지 보내기

        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    //사진 삭제하기
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<List<PhotoDto>> deletePhoto(@PathVariable("albumId") final long albumId,
                                                      @RequestBody final ChangePhotoDto changePhotoDto){
        photoService.deletePhoto(changePhotoDto.getPhotoIds()); // 삭제
        List<PhotoDto> photos = photoService.getPhotos(albumId);

        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

}
