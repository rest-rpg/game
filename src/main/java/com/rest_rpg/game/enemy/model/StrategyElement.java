package com.rest_rpg.game.enemy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.openapitools.model.ElementAction;
import org.openapitools.model.ElementEvent;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ElementEvent elementEvent;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ElementAction elementAction;

    @ManyToMany(mappedBy = "strategyElements")
    private Set<Enemy> enemy = new HashSet<>();

    private int priority;
}
