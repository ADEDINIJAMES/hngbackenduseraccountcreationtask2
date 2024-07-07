package com.tumtech.groupcreationuserhngbackendtsk2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNameNotFoundException extends Exception {
    public UserNameNotFoundException(String message){
        super(message);
    }
}
