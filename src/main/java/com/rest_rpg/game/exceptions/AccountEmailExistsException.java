package com.rest_rpg.game.exceptions;

import org.openapitools.model.ErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountEmailExistsException extends ResponseStatusException {

    public AccountEmailExistsException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public AccountEmailExistsException() {
        this(ErrorCodes.ACCOUNT_EMAIL_EXISTS.toString());
    }
}
