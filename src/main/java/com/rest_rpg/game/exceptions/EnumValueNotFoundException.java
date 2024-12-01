package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EnumValueNotFoundException extends ResponseStatusException {

    public EnumValueNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorCodes.ENUM_VALUE_NOT_FOUND.toString());
    }
}
