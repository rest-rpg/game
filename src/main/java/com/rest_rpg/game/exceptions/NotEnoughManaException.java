package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotEnoughManaException extends ResponseStatusException {

    public NotEnoughManaException() {
        super(HttpStatus.FORBIDDEN, ErrorCodes.NOT_ENOUGH_MANA.toString());
    }
}
