package com.cinelog.exception;

public class DiaryEntryNotFoundException extends RuntimeException {

    public DiaryEntryNotFoundException(String message) {
        super(message);
    }
}
