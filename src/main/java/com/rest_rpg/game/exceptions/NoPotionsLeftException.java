package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoPotionsLeftException extends ResponseStatusException {

    public NoPotionsLeftException() {
        super(HttpStatus.NOT_FOUND, ErrorCodes.NO_POTIONS_LEFT.toString());
    }
}
