package com.rest_rpg.game.character;

import com.rest_rpg.game.character.model.Character;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rest_rpg.game.character.model.QCharacter;
import jakarta.persistence.EntityManager;

public class CharacterRepositoryCustomImpl implements CharacterRepositoryCustom {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public CharacterRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Character getWithEntityGraph(long id, String graph) {
        QCharacter character = QCharacter.character;
        return queryFactory.select(character)
                .from(character)
                .where(character.id.eq(id))
                .setHint("javax.persistence.fetchgraph", entityManager.getEntityGraph(graph))
                .fetchOne();
    }
}
