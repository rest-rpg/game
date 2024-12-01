package com.rest_rpg.game.enemy;

import com.rest_rpg.game.enemy.model.Enemy;
import com.rest_rpg.game.exceptions.EnemyDoesNotExistException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, Long> {

    default Enemy getById(long id) {
        return findById(id).orElseThrow(EnemyDoesNotExistException::new);
    }

    List<Enemy> findByDeletedFalse();

    boolean existsByName(String name);
}
