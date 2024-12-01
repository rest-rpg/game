package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ImageDoesNotExistException extends ResponseStatusException {

    public ImageDoesNotExistException() {
        super(HttpStatus.NOT_FOUND, ErrorCodes.IMAGE_DOES_NOT_EXIST.toString());
    }
}
