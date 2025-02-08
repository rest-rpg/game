package com.rest_rpg.game.skill;

import com.rest_rpg.game.skill.model.QSkill;
import com.rest_rpg.game.skill.model.Skill;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.SkillSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QPageRequest;

import static com.rest_rpg.game.helpers.SearchHelper.getQSort;

public class SkillRepositoryCustomImpl implements SkillRepositoryCustom {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final PathBuilderFactory pathBuilderFactory;

    public SkillRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.pathBuilderFactory = new PathBuilderFactory();
    }

    @Override
    public Page<Skill> findSkills(@NotNull SkillSearchRequest request, @NotNull Pageable pageable) {
        var skill = QSkill.skill;
        var predicate = buildPredicate(skill, request);

        var pageRequest = QPageRequest.of(request.getPagination().getPageNumber(), request.getPagination().getElements(), getQSort(request.getPagination()));

        var idQuery = queryFactory.select(skill.id)
                .from(skill)
                .where(predicate);

        var querydsl = new Querydsl(entityManager, pathBuilderFactory.create(QSkill.class));
        querydsl.applyPagination(pageRequest, idQuery);

        var query = queryFactory.select(skill)
                .from(skill)
                .where(skill.id.in(idQuery.fetch()));

        querydsl.applySorting(pageRequest.getSort(), query);

        var skills = query.fetch();
        var total = idQuery.fetchCount();

        return new PageImpl<>(skills, pageable, total);
    }

    private BooleanBuilder buildPredicate(@NotNull QSkill skill, @NotNull SkillSearchRequest request) {
        var predicate = new BooleanBuilder();

        if (request.getIdNotIn() != null) {
            predicate.and(skill.id.notIn(request.getIdNotIn()));
        }

        if (request.getNameLike() != null) {
            predicate.and(skill.name.contains(request.getNameLike()));
        }

        if (request.getSkillTypeIn() != null) {
            predicate.and(skill.type.in(request.getSkillTypeIn()));
        }

        if (request.getSkillEffectIn() != null) {
            predicate.and(skill.effect.in(request.getSkillEffectIn()));
        }

        if (request.getCharacterClassIn() != null) {
            predicate.and(skill.characterClass.in(request.getCharacterClassIn()));
        }

        return predicate;
    }
}
