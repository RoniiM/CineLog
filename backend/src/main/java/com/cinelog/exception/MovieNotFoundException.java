package com.cinelog.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String message) {
        super(message);
    }
}
