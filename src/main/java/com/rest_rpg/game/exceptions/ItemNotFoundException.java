package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {

    public ItemNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorCodes.ITEM_NOT_FOUND.toString());
    }
}
