package com.rest_rpg.game.statistics;

import com.rest_rpg.game.exceptions.CharacterNotFoundException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    @EntityGraph(Statistics.STATISTICS_DETAILS)
    Optional<Statistics> findByCharacter_Id(@NonNull Long characterId);

    default Statistics getStatisticsByCharacterId(long characterId) {
        return findByCharacter_Id(characterId).orElseThrow(CharacterNotFoundException::new);
    }
}
