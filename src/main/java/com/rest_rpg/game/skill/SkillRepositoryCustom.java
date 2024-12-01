package com.rest_rpg.game.skill;

import com.rest_rpg.game.skill.model.Skill;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.SkillSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepositoryCustom {

    Page<Skill> findSkills(@NotNull SkillSearchRequest request, @NotNull Pageable pageable);
}
