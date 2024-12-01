package com.rest_rpg.game.work.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class WorkCreateRequestDto {

    @NotBlank
    String name;

    @Positive
    int wage;

    @Positive
    int workMinutes;
}
