package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyVerifiedException extends ResponseStatusException {

    public UserAlreadyVerifiedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public UserAlreadyVerifiedException() {
        this(ErrorCodes.ACCOUNT_ALREADY_VERIFIED.toString());
    }
}
