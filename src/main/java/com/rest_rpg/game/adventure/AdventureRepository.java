package com.rest_rpg.game.adventure;

import com.rest_rpg.game.adventure.model.Adventure;
import com.rest_rpg.game.exceptions.AdventureNotFoundException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdventureRepository extends JpaRepository<Adventure, Long>, AdventureRepositoryCustom {

    boolean existsByNameIgnoreCaseAndDeletedFalse(@NotBlank String name);

    Optional<Adventure> findByNameIgnoreCaseAndDeletedFalse(@NotBlank String name);

    @EntityGraph(value = Adventure.ADVENTURE_DETAILS)
    default Adventure getAdventureById(long adventureId) {
        var adventure = findById(adventureId).orElseThrow(AdventureNotFoundException::new);
        if (adventure.isDeleted()) {
            throw new AdventureNotFoundException();
        }
        return adventure;
    }
}
