package com.example.photoalbum.mapper;


import com.example.photoalbum.domain.User;
import com.example.photoalbum.dto.UserDto;

public class UserMapper {

    public static UserDto convertToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUserName(user.getUserName());
        return userDto;
    }

    public static User convertToModel(UserDto userDto){
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setEmail(userDto.getEmail());
        user.setUserName(userDto.getUserName());
        user.setPassword(userDto.getPassword());
        return user;
    }
}
