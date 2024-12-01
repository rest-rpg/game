package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmptyJwtException extends ResponseStatusException {

    public EmptyJwtException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public EmptyJwtException() {
        this(ErrorCodes.EMPTY_JWT.toString());
    }
}
