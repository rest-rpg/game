package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotEnoughGoldException extends ResponseStatusException {

    public NotEnoughGoldException() {
        super(HttpStatus.FORBIDDEN, ErrorCodes.NOT_ENOUGH_GOLD.toString());
    }
}
