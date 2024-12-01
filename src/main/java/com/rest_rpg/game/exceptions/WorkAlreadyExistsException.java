package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WorkAlreadyExistsException extends ResponseStatusException {

    public WorkAlreadyExistsException() {
        super(HttpStatus.CONFLICT, ErrorCodes.WORK_ALREADY_EXISTS.toString());
    }
}
