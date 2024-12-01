package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class JwtExpiredException extends ResponseStatusException {

    public JwtExpiredException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public JwtExpiredException() {
        this(ErrorCodes.JWT_EXPIRED.toString());
    }
}
