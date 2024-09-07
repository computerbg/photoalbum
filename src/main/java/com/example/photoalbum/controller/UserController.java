package com.example.photoalbum.controller;

import com.example.photoalbum.dto.UserDto;
import com.example.photoalbum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/back_end")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //로그인
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity<UserDto> login(@RequestBody final UserDto userDto){
        UserDto loginUserDto = userService.login(userDto);
        return new ResponseEntity<>(loginUserDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/join",method = RequestMethod.POST)
    public ResponseEntity<UserDto> joinMembership(@RequestBody final UserDto userDto){
        UserDto joinUserDto = userService.joinMembership(userDto);
        return new ResponseEntity<>(joinUserDto,HttpStatus.OK);
    }


}
