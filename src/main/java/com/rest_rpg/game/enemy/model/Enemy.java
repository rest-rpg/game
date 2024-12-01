package com.rest_rpg.game.enemy.model;

import com.rest_rpg.game.adventure.model.Adventure;
import com.rest_rpg.game.fight.model.Fight;
import com.rest_rpg.game.skill.model.Skill;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Enemy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    private String name;

    private int hp;

    private int mana;

    private int damage;

    @Nullable
    @OneToMany(mappedBy = "enemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Fight> fights = new HashSet<>();

    @Nullable
    @OneToMany(mappedBy = "enemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Adventure> adventures = new HashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int skillLevel;

    private int numberOfPotions;

    @NotEmpty
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "strategy_element_enemy",
            joinColumns = @JoinColumn(name = "enemy_id"),
            inverseJoinColumns = @JoinColumn(name = "strategy_element_id"))
    private Set<StrategyElement> strategyElements = new HashSet<>();

    private boolean deleted;

    public static Enemy of(@NotNull @Valid EnemyCreateRequestDto dto, @NotNull Skill skill) {
        return Enemy.builder()
                .hp(dto.getHp())
                .mana(dto.getMana())
                .damage(dto.getDamage())
                .name(dto.getName())
                .numberOfPotions(dto.getNumberOfPotions())
                .skill(skill)
                .skillLevel(dto.getSkillLevel())
                .deleted(false)
                .build();
    }

    public void usePotion() {
        numberOfPotions = Math.max(0, numberOfPotions - 1);
    }
}
