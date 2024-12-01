package com.rest_rpg.game.enemy;

import com.rest_rpg.game.enemy.model.StrategyElement;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.ElementAction;
import org.openapitools.model.ElementEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyElementRepository extends JpaRepository<StrategyElement, Long> {

    List<StrategyElement> findByElementEventAndElementActionAndPriority(@NotNull ElementEvent elementEvent, @NotNull ElementAction elementAction, int priority);
}