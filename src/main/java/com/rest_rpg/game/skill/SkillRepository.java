package com.rest_rpg.game.skill;

import com.rest_rpg.game.exceptions.SkillNotFoundException;
import com.rest_rpg.game.skill.model.Skill;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, SkillRepositoryCustom {

    boolean existsByNameIgnoreCase(@NotBlank String name);

    List<Skill> findByDeletedFalse();

    default Skill get(long id) {
        return findById(id).orElseThrow(SkillNotFoundException::new);
    }
}
