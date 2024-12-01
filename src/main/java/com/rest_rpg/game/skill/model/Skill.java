package com.rest_rpg.game.skill.model;

import com.rest_rpg.game.character_skill.CharacterSkill;
import com.rest_rpg.game.enemy.model.Enemy;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.model.CharacterClass;
import org.openapitools.model.SkillEffect;
import org.openapitools.model.SkillType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    private String name;

    private int manaCost;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SkillType type;

    private float multiplier;

    private float multiplierPerLevel;

    @Nullable
    @Enumerated(EnumType.STRING)
    private SkillEffect effect;

    private int effectDuration;

    private int effectDurationPerLevel;

    private float effectMultiplier;

    private float effectMultiplierPerLevel;

    @Embedded
    private SkillTraining skillTraining;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    @OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
    private Set<Enemy> enemy = new HashSet<>();

    @OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
    private Set<CharacterSkill> characters = new HashSet<>();

    private boolean magicDamage;

    private boolean deleted;

    public static Skill of(@Valid SkillCreateRequestDto dto) {
        return builder()
                .name(dto.getName())
                .manaCost(dto.getManaCost())
                .type(dto.getType())
                .multiplier(dto.getMultiplier())
                .multiplierPerLevel(dto.getMultiplierPerLevel())
                .effect(dto.getEffect())
                .effectDuration(dto.getEffectDuration())
                .effectDurationPerLevel(dto.getEffectDurationPerLevel())
                .effectMultiplier(dto.getEffectMultiplier())
                .effectMultiplierPerLevel(dto.getEffectMultiplierPerLevel())
                .skillTraining(Skill.SkillTraining.builder()
                        .goldCost(dto.getGoldCost())
                        .statisticPointsCost(dto.getStatisticPointsCost())
                        .build())
                .characterClass(dto.getCharacterClass())
                .build();
    }

    public float getDamageMultiplier(int skillLevel) {
        return multiplier + multiplierPerLevel * skillLevel;
    }

    public float getFinalEffectMultiplier(int skillLevel) {
        return effectMultiplier + effectMultiplierPerLevel * skillLevel;
    }

    public int getFinalEffectDuration(int skillLevel) {
        return effectDuration + effectDurationPerLevel * skillLevel;
    }

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SkillTraining {

        private int goldCost;

        private int statisticPointsCost;
    }
}
