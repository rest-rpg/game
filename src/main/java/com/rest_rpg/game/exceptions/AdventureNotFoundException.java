package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AdventureNotFoundException extends ResponseStatusException {

    public AdventureNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorCodes.ADVENTURE_NOT_FOUND.toString());
    }
}
