package com.rest_rpg.game.work.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String name;

    private int wage;

    private int workMinutes;

    private boolean deleted;

    public static Work of(@NotNull @Valid WorkCreateRequestDto dto) {
        return builder()
                .name(dto.getName())
                .wage(dto.getWage())
                .workMinutes(dto.getWorkMinutes())
                .build();
    }
}
