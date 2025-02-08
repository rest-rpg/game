package com.rest_rpg.game.statistics;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.exceptions.NotEnoughManaException;
import com.rest_rpg.game.exceptions.NotEnoughSkillPointsException;
import com.rest_rpg.game.fight.FightService;
import com.rest_rpg.game.item.model.Item;
import com.rest_rpg.game.statistics.dto.StatisticsUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.model.CharacterRace;

import java.util.Optional;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(name = Statistics.STATISTICS_DETAILS,
        attributeNodes = {
                @NamedAttributeNode(value = "character", subgraph = "character-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "character-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "equipment", subgraph = "equipment-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "equipment-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("armor"),
                                @NamedAttributeNode("weapon")
                        }
                )
        }
)
public class Statistics {

    public static final String STATISTICS_DETAILS = "statistic-details-graph";

    public static final int HP_MULTIPLIER = 10;
    public static final int MANA_MULTIPLIER = 10;
    public static final int DAMAGE_MULTIPLIER = 2;
    public static final int MAGIC_DAMAGE_MULTIPLIER = 3;
    public static final int START_FREE_STATISTICS_POINTS = 50;
    public static final int STATISTICS_POINTS_PER_LEVEL = 10;
    public static final int CHARACTER_RACE_BONUS = 5;
    public static final int XP_TO_NEXT_LEVEL_BASE = 250;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private int maxHp;

    private int currentHp;

    private int maxMana;

    private int currentMana;

    private int currentXp;

    private int currentLevel;

    private int freeStatisticPoints;

    private int strength;

    private int dexterity;

    private int constitution;

    private int intelligence;

    @OneToOne(mappedBy = "statistics")
    private Character character;

    private boolean deleted;

    public static Statistics init() {
        return Statistics.builder()
                .maxHp(100)
                .currentHp(100)
                .maxMana(100)
                .currentMana(100)
                .currentXp(0)
                .currentLevel(1)
                .freeStatisticPoints(START_FREE_STATISTICS_POINTS)
                .strength(10)
                .dexterity(10)
                .constitution(10)
                .intelligence(10)
                .build();
    }

    public void train(@NotNull @Valid StatisticsUpdateRequestDto dto) {
        var sum = dto.getConstitution() + dto.getDexterity() + dto.getStrength() + dto.getIntelligence();
        if (sum > freeStatisticPoints) {
            throw new NotEnoughSkillPointsException();
        }

        this.strength += dto.getStrength();
        this.dexterity += dto.getDexterity();
        this.constitution += dto.getConstitution();
        this.intelligence += dto.getIntelligence();
        this.freeStatisticPoints -= sum;
        updateStats();
    }

    public int getDamage() {
        return this.strength * DAMAGE_MULTIPLIER +
                Optional.ofNullable(character.getEquipment().getWeapon()).map(Item::getPower).orElse(0);
    }

    public int getMagicDamage() {
        return this.intelligence * MAGIC_DAMAGE_MULTIPLIER;
    }

    public int getArmor() {
        return Optional.ofNullable(character.getEquipment().getArmor()).map(Item::getPower).orElse(0);
    }

    public float getDodgeChance() {
        return criticalDodgeChance(this.dexterity);
    }

    public float getCriticalChance() {
        return criticalDodgeChance(this.dexterity);
    }

    public int getXpToNextLevel() {
        return XP_TO_NEXT_LEVEL_BASE * currentLevel * currentLevel;
    }

    public void addStatistics(@NotNull @Valid StatisticsUpdateRequestDto dto, @NotNull CharacterRace race) {
        var sum = dto.getConstitution() + dto.getDexterity() + dto.getStrength() + dto.getIntelligence();
        if (sum > freeStatisticPoints) {
            throw new NotEnoughSkillPointsException();
        }

        this.strength = dto.getStrength();
        this.dexterity = dto.getDexterity();
        this.constitution = dto.getConstitution();
        this.intelligence = dto.getIntelligence();
        this.freeStatisticPoints -= sum;
        updateStats();
        setRaceBonus(race);
    }

    public void updateStats() {
        var previousMaxHp = this.maxHp;
        this.maxHp = this.constitution * HP_MULTIPLIER;
        this.currentHp += this.maxHp - previousMaxHp;
        var previousMaxMana = this.maxMana;
        this.maxMana = this.intelligence * MANA_MULTIPLIER;
        this.currentMana += this.maxMana - previousMaxMana;
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public void useMana(int mana) {
        if (mana > currentMana) {
            throw new NotEnoughManaException();
        }
        currentMana -= mana;
    }

    public void heal(int percent) {
        currentHp += maxHp * percent / 100;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    }

    public void regenerateManaPerTurn() {
        currentMana += maxMana * FightService.MANA_REGENERATION_PERCENT_PER_TURN / 100;
        if (currentMana > maxMana) {
            currentMana = maxMana;
        }
    }

    public void earnXp(int xp) {
        currentXp += xp;
        while (currentXp >= getXpToNextLevel()) {
            currentLevel++;
            freeStatisticPoints += STATISTICS_POINTS_PER_LEVEL;
        }
    }

    public void useStatisticPoints(int points) {
        if (points > freeStatisticPoints) {
            throw new NotEnoughSkillPointsException();
        }
        freeStatisticPoints -= points;
    }

    private void setRaceBonus(CharacterRace race) {
        switch (race) {
            case ELF -> this.dexterity += CHARACTER_RACE_BONUS;
            case DWARF -> this.constitution += CHARACTER_RACE_BONUS;
            case HUMAN -> this.strength += CHARACTER_RACE_BONUS;
        }
    }

    private float criticalDodgeChance(int statistic) {
        double k = 0.01;
        return (float) (100 * (1 - Math.exp(-k * statistic)));
    }
}
