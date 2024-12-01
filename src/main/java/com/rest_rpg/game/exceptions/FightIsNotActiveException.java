package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FightIsNotActiveException extends ResponseStatusException {

    public FightIsNotActiveException() {
        super(HttpStatus.BAD_REQUEST, ErrorCodes.FIGHT_IS_NOT_ACTIVE.toString());
    }
}
