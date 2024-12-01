package com.rest_rpg.game.adventure;

import com.rest_rpg.game.adventure.model.Adventure;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.AdventureSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface AdventureRepositoryCustom {

    Page<Adventure> findAdventures(@NotNull AdventureSearchRequest request, @NotNull Pageable pageable);
}
