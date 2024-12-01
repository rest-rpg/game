package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RefreshTokenNotFoundException extends ResponseStatusException {

    public RefreshTokenNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public RefreshTokenNotFoundException() {
        this(ErrorCodes.REFRESH_TOKEN_NOT_FOUND.toString());
    }
}
