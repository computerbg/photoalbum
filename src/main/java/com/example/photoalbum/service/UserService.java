package com.example.photoalbum.service;

import com.example.photoalbum.domain.User;
import com.example.photoalbum.dto.UserDto;
import com.example.photoalbum.exception.InvalidIdOrPasswordException;
import com.example.photoalbum.mapper.UserMapper;
import com.example.photoalbum.repository.AlbumRepository;
import com.example.photoalbum.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AlbumRepository albumRepository;

    public UserDto login(UserDto userDto){
        Optional<User> user = userRepository.findById(userDto.getUserId());

        if(user.isEmpty() || !user.get().getPassword().equals(userDto.getPassword())) {
            throw new InvalidIdOrPasswordException("Incorrect ID or Password");
        }
        User loginUser = user.get();
        loginUser.updateLoginTime();
        userRepository.save(loginUser);

        UserDto loginUserDto = UserMapper.convertToDto(loginUser);
        loginUserDto.setCount(albumRepository.countByUser_UserId(loginUser.getUserId()));

        return loginUserDto;
    }

    public  UserDto joinMembership(UserDto userDto){
        Optional<User> user = userRepository.findById(userDto.getUserId());
        if(user.isPresent()){
            throw new ValidationException("존재하는 ID입니다.");
        } // 추후에 username이 비었거나 email이 비었을 때 던져주는 예외처리 추가 ?
        User joinUser = UserMapper.convertToModel(userDto);
        User saveUser = userRepository.save(joinUser);

        return UserMapper.convertToDto(saveUser);

    }

}
