package com.movieapp.movie_api.exception;

public class EmptyFileException extends RuntimeException{

    public EmptyFileException(String message){
        super(message);
    }

}
