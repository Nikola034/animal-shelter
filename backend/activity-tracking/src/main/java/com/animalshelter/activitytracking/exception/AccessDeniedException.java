package com.animalshelter.activitytracking.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
