package com.rest_rpg.game.character_skill;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterSkillKey implements Serializable {

    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "skill_id")
    private Long skillId;
}
