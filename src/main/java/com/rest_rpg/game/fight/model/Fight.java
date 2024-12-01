package com.rest_rpg.game.fight.model;

import com.rest_rpg.game.enemy.model.Enemy;
import com.rest_rpg.game.fight.FightService;
import com.rest_rpg.game.fight_effect.FightEffect;
import com.rest_rpg.game.item.ItemService;
import com.rest_rpg.game.occupation.Occupation;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(name = Fight.TEST_GRAPH,
        attributeNodes = {
                @NamedAttributeNode("fightEffects"),
                @NamedAttributeNode(value = "enemy", subgraph = "skill-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "skill-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("skill")
                        }
                )
        }
)
public class Fight {

    public static final String TEST_GRAPH = "TEST_GRAPH";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(mappedBy = "fight", fetch = FetchType.EAGER)
    private Occupation occupation;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enemy_id")
    private Enemy enemy;

    private int enemyCurrentHp;

    private int enemyCurrentMana;

    @OneToMany(mappedBy = "fight", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FightEffect> fightEffects = new HashSet<>();

    private boolean isActive;

    private boolean deleted;

    public void dealDamageToEnemy(int damage) {
        enemyCurrentHp -= damage;
        if (enemyCurrentHp < 0) {
            setEnemyCurrentHp(0);
        }
    }

    public void healEnemy() {
        setEnemyCurrentHp(Math.min(Optional.ofNullable(enemy).map(Enemy::getHp).orElseThrow(), enemyCurrentHp + ItemService.POTION_HEAL_PERCENT * enemy.getHp() / 100));
    }

    public void enemyUseMana() {
        setEnemyCurrentMana(Math.max(0, enemyCurrentMana - Optional.ofNullable(enemy).map(Enemy::getSkill).orElseThrow().getManaCost()));
    }

    public void regenerateEnemyManaPerTurn() {
        int maxMana = Optional.ofNullable(enemy).map(Enemy::getMana).orElseThrow();
        setEnemyCurrentMana(Math.min(maxMana, enemyCurrentMana + maxMana * FightService.MANA_REGENERATION_PERCENT_PER_TURN));
    }

    public void addFightEffect(@NotNull FightEffect fightEffect) {
        if (fightEffect.getId() != null) {
            var effects = fightEffects.stream().filter(fe -> !Objects.equals(fe.getId(), fightEffect.getId())).collect(Collectors.toSet());
            effects.add(fightEffect);
            setFightEffects(effects);
        } else {
            fightEffects.add(fightEffect);
        }
    }
}
