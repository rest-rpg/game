package com.rest_rpg.game.character_skill;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.skill.model.Skill;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class CharacterSkill {

    @EmbeddedId
    private CharacterSkillKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("characterId")
    @JoinColumn(name = "character_id")
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int level;

    private boolean deleted;

    public static CharacterSkill newSkill(@NotNull Skill skill, @NotNull Character character) {
        return CharacterSkill.builder()
                .id(new CharacterSkillKey(character.getId(), skill.getId()))
                .skill(skill)
                .character(character)
                .level(1)
                .build();
    }

    public void upgrade() {
        level += 1;
    }
}
