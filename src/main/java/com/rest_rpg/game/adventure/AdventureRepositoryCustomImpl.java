package com.rest_rpg.game.adventure;

import com.rest_rpg.game.adventure.model.Adventure;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rest_rpg.game.adventure.model.QAdventure;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.AdventureSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QPageRequest;

import static com.rest_rpg.game.helpers.SearchHelper.getQSort;

public class AdventureRepositoryCustomImpl implements AdventureRepositoryCustom {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final PathBuilderFactory pathBuilderFactory;

    public AdventureRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.pathBuilderFactory = new PathBuilderFactory();
    }

    @Override
    public Page<Adventure> findAdventures(@NotNull AdventureSearchRequest request, @NotNull Pageable pageable) {
        var adventure = QAdventure.adventure;
        var predicate = buildPredicate(adventure, request);

        var pageRequest = QPageRequest.of(request.getPagination().getPageNumber(), request.getPagination().getElements(), getQSort(request.getPagination()));

        var idQuery = queryFactory.select(adventure.id)
                .from(adventure)
                .where(predicate);

        var querydsl = new Querydsl(entityManager, pathBuilderFactory.create(QAdventure.class));
        querydsl.applyPagination(pageRequest, idQuery);

        var query = queryFactory.select(adventure)
                .from(adventure)
                .where(adventure.id.in(idQuery.fetch()))
                .setHint("javax.persistence.fetchgraph", entityManager.getEntityGraph(Adventure.ADVENTURE_BASIC));

        querydsl.applySorting(pageRequest.getSort(), query);

        var skills = query.fetch();
        var total = idQuery.fetchCount();

        return new PageImpl<>(skills, pageable, total);
    }

    private BooleanBuilder buildPredicate(@NotNull QAdventure adventure, @NotNull AdventureSearchRequest request) {
        var predicate = new BooleanBuilder();

        if (request.getNameLike() != null) {
            predicate.and(adventure.name.contains(request.getNameLike()));
        }
        if (request.getAdventureTimeGreaterThanOrEqual() != null) {
            predicate.and(adventure.adventureTimeInMinutes.goe(request.getAdventureTimeGreaterThanOrEqual()));
        }
        if (request.getAdventureTimeLessThanOrEqual() != null) {
            predicate.and(adventure.adventureTimeInMinutes.loe(request.getAdventureTimeLessThanOrEqual()));
        }
        if (request.getXpGreaterThanOrEqual() != null) {
            predicate.and(adventure.xpForAdventure.goe(request.getXpGreaterThanOrEqual()));
        }
        if (request.getXpLessThanOrEqual() != null) {
            predicate.and(adventure.xpForAdventure.loe(request.getXpLessThanOrEqual()));
        }
        if (request.getGoldGreaterThanOrEqual() != null) {
            predicate.and(adventure.goldForAdventure.goe(request.getGoldGreaterThanOrEqual()));
        }
        if (request.getGoldLessThanOrEqual() != null) {
            predicate.and(adventure.goldForAdventure.loe(request.getGoldLessThanOrEqual()));
        }
        if (request.getEnemyNameLike() != null) {
            predicate.and(adventure.enemy.name.contains(request.getEnemyNameLike()));
        }
        predicate.and(adventure.deleted.eq(false));

        return predicate;
    }
}
