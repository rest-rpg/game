package com.rest_rpg.game.character;

import com.rest_rpg.game.character.model.Character;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepositoryCustom {

    Character getWithEntityGraph(long id, @NotEmpty String graph);
}
