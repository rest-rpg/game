package com.rest_rpg.game.fight;

import com.rest_rpg.game.fight.model.Fight;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FightRepository extends JpaRepository<Fight, Long> {

    @EntityGraph(Fight.TEST_GRAPH)
    Optional<Fight> readById(Long id);
}
