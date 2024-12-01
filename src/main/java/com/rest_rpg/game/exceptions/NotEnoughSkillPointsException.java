package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotEnoughSkillPointsException extends ResponseStatusException {

    public NotEnoughSkillPointsException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public NotEnoughSkillPointsException() {
        this(ErrorCodes.NOT_ENOUGH_SKILL_POINTS.toString());
    }
}
