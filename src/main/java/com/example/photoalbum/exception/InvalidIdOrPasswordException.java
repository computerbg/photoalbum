package com.example.photoalbum.exception;

public class InvalidIdOrPasswordException extends RuntimeException{
    public InvalidIdOrPasswordException(String message){
        super(message);
    }
}
