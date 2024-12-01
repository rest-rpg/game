package com.rest_rpg.game.enemy;

import com.rest_rpg.game.enemy.model.Enemy;
import com.rest_rpg.game.enemy.model.EnemyCreateRequestDto;
import com.rest_rpg.game.enemy.model.StrategyElementCreateRequestDto;
import com.rest_rpg.game.skill.SkillMapper;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.openapitools.model.EnemyCreateRequest;
import org.openapitools.model.EnemyLite;
import org.openapitools.model.EnemyLites;
import org.openapitools.model.StrategyElementCreateRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = SkillMapper.class)
public interface EnemyMapper {

    @Mapping(source = "enemyStrategy", target = "strategyElementCreateRequest", qualifiedByName = "strategyElementCreateRequestDto")
    EnemyCreateRequestDto toDto(EnemyCreateRequest source);

    EnemyLite toLite(Enemy source);

    StrategyElementCreateRequestDto toDto(StrategyElementCreateRequest source);

    @Named("strategyElementCreateRequestDto")
    default List<StrategyElementCreateRequestDto> strategyElementCreateRequestDto(List<StrategyElementCreateRequest> requests) {
        return requests.stream().map(this::toDto).collect(Collectors.toList());
    }

    default EnemyLites toLites(@NotNull List<Enemy> source) {
        return new EnemyLites().content(source.stream().map(this::toLite).toList());
    }
}
