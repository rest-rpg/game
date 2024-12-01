package com.rest_rpg.game.character;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.exceptions.CharacterNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long>, CharacterRepositoryCustom {

    boolean existsByNameIgnoreCase(@NotNull String name);

    @EntityGraph(Character.CHARACTER_BASIC)
    List<Character> findByUserId(long userId);

    @Override
    @NotNull
    @EntityGraph(Character.CHARACTER_BASIC)
    Optional<Character> findById(@NotNull Long id);

    @EntityGraph(Character.CHARACTER_TEST)
    Optional<Character> readById(Long id);

    default Character getCharacterById(long id) {
        return findById(id).orElseThrow(CharacterNotFoundException::new);
    }

    default Character getCharacterWithTestGraphById(long id) {
        return readById(id).orElseThrow(CharacterNotFoundException::new);
    }
}
