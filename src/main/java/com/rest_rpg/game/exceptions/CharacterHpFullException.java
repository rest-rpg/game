package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CharacterHpFullException extends ResponseStatusException {

    public CharacterHpFullException() {
        super(HttpStatus.NOT_FOUND, ErrorCodes.CHARACTER_HP_FULL.toString());
    }
}
