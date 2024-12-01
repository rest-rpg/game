package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public UserNotFoundException() {
        this(ErrorCodes.USER_NOT_FOUND.toString());
    }
}
