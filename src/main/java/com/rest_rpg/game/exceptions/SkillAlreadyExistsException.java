package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SkillAlreadyExistsException extends ResponseStatusException {

    public SkillAlreadyExistsException() {
        super(HttpStatus.CONFLICT, ErrorCodes.SKILL_ALREADY_EXISTS.toString());
    }
}
