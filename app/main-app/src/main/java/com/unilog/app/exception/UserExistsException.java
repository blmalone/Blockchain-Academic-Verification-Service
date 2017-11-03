package com.unilog.app.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class UserExistsException extends Exception {
    public UserExistsException() {
        super();
    }
}



