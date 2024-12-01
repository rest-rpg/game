package com.rest_rpg.game.fight;

import com.rest_rpg.game.character.CharacterRepository;
import com.rest_rpg.game.character.CharacterService;
import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.enemy.model.Enemy;
import com.rest_rpg.game.enemy.model.StrategyElement;
import com.rest_rpg.game.equipment.Equipment;
import com.rest_rpg.game.exceptions.AdventureNotFoundException;
import com.rest_rpg.game.exceptions.CharacterHpFullException;
import com.rest_rpg.game.exceptions.FightIsNotActiveException;
import com.rest_rpg.game.exceptions.NoPotionsLeftException;
import com.rest_rpg.game.exceptions.NotEnoughManaException;
import com.rest_rpg.game.exceptions.SkillNotFoundException;
import com.rest_rpg.game.fight.model.Fight;
import com.rest_rpg.game.fight_effect.FightEffect;
import com.rest_rpg.game.skill.SkillRepository;
import com.rest_rpg.game.statistics.Statistics;
import com.rest_rpg.user.feign.UserInternalClient;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.ElementEvent;
import org.openapitools.model.FightActionRequest;
import org.openapitools.model.FightActionResponse;
import org.openapitools.model.FightDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class FightService {

    public static int MANA_REGENERATION_PERCENT_PER_TURN = 10;

    private final CharacterRepository characterRepository;
    private final SkillRepository skillRepository;
    private final UserInternalClient userInternalClient;
    private final FightMapper fightMapper;

    private final AtomicBoolean playerStunned = new AtomicBoolean(false);
    private final AtomicBoolean enemyStunned = new AtomicBoolean(false);
    private final AtomicReference<Float> playerDamageMultiplier = new AtomicReference<>((float) 1);
    private final AtomicReference<Float> enemyDamageMultiplier = new AtomicReference<>((float) 1);
    private final AtomicReference<Float> playerDefenceMultiplier = new AtomicReference<>((float) 1);
    private final AtomicReference<Float> enemyDefenceMultiplier = new AtomicReference<>((float) 1);

    @Transactional
    public FightDetails getFight(long characterId) {
        var character = characterRepository.getWithEntityGraph(characterId, Character.CHARACTER_FIGHT);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        return fightMapper.toDetails(character.getOccupation().getFight());
    }

    @Transactional
    public FightActionResponse performActionInFight(@NotNull FightActionRequest fightActionRequest) {
        playerStunned.set(false);
        enemyStunned.set(false);
        playerDamageMultiplier.set(1f);
        enemyDamageMultiplier.set(1f);
        playerDefenceMultiplier.set(1f);
        enemyDefenceMultiplier.set(1f);
        var request = fightMapper.toDto(fightActionRequest);
        var character = characterRepository.getWithEntityGraph(request.getCharacterId(), Character.CHARACTER_FIGHT_ACTION);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        var fight = character.getOccupation().getFight();
        checkIfFightIsActive(fight);
        var response = new FightActionResponse();

        applyFightEffects(fight, character);

        if (!playerStunned.get()) {
            switch (request.getAction()) {
                case NORMAL_ATTACK -> attack(character, response, fight);
                case USE_POTION -> usePotion(character);
                case SPECIAL_ATTACK -> specialAttack(character, fight, response, fightActionRequest.getSkillId());
            }
        }
        if (!enemyStunned.get()) {
            enemyTurn(response, fight, character);
        }

        if (fight.getEnemyCurrentHp() <= 0) {
            winFight(fight, character, response);
        } else if (character.getStatistics().getCurrentHp() <= 0) {
            loseFight(fight, character, response);
        }

        if (response.getPlayerWon() == null) {
            character.getStatistics().regenerateManaPerTurn();
            character.getOccupation().getFight().regenerateEnemyManaPerTurn();
        }

        character = characterRepository.save(character);
        response.setPlayerCurrentMana(character.getStatistics().getCurrentMana());
        response.setPlayerCurrentHp(character.getStatistics().getCurrentHp());
        response.setFight(fightMapper.toDetails(character.getOccupation().getFight()));
        response.setPlayerPotions(character.getEquipment().getHealthPotions());
        return response;
    }

    private void applyFightEffects(@NotNull Fight fight, @NotNull Character character) {
        if (fight.getFightEffects() != null) {
            fight.getFightEffects().stream().filter(effect -> effect.getDuration() > 0).forEach(effect -> {
                effect.passTurn();
                switch (effect.getSkillEffect()) {
                    case BLEEDING, BURNING -> {
                        if (effect.isPlayerEffect()) {
                            character.getStatistics().takeDamage(
                                    Math.round(character.getStatistics().getMaxHp() * effect.getEffectMultiplier()));
                        } else {
                            var enemyMaxHp = Optional.ofNullable(fight.getEnemy()).map(Enemy::getHp).orElseThrow();
                            fight.dealDamageToEnemy(Math.round(enemyMaxHp * effect.getEffectMultiplier()));
                        }
                    }
                    case STUNNED -> {
                        if (effect.isPlayerEffect()) {
                            playerStunned.set(true);
                        } else {
                            enemyStunned.set(true);
                        }
                    }
                    case WEAKNESS -> {
                        if (effect.isPlayerEffect()) {
                            playerDamageMultiplier.set(Math.max(0.1f, playerDamageMultiplier.get() - effect.getEffectMultiplier()));
                        } else {
                            enemyDamageMultiplier.set(Math.max(0.1f, enemyDamageMultiplier.get() - effect.getEffectMultiplier()));
                        }
                    }
                    case DAMAGE_BOOST -> {
                        if (effect.isPlayerEffect()) {
                            playerDamageMultiplier.set(Math.max(0.1f, playerDamageMultiplier.get() + effect.getEffectMultiplier()));
                        } else {
                            enemyDamageMultiplier.set(Math.max(0.1f, enemyDamageMultiplier.get() + effect.getEffectMultiplier()));
                        }
                    }
                    case DEFENCE_BOOST -> {
                        if (effect.isPlayerEffect()) {
                            playerDefenceMultiplier.set(Math.max(0.1f, playerDefenceMultiplier.get() + effect.getEffectMultiplier()));
                        } else {
                            enemyDefenceMultiplier.set(Math.max(0.1f, enemyDefenceMultiplier.get() + effect.getEffectMultiplier()));
                        }
                    }
                    case DAMAGE_DEFENCE_BOOST -> {
                        if (effect.isPlayerEffect()) {
                            playerDamageMultiplier.set(Math.max(0.1f, playerDamageMultiplier.get() + effect.getEffectMultiplier()));
                            playerDefenceMultiplier.set(Math.max(0.1f, playerDefenceMultiplier.get() + effect.getEffectMultiplier()));
                        } else {
                            enemyDamageMultiplier.set(Math.max(0.1f, enemyDamageMultiplier.get() + effect.getEffectMultiplier()));
                            enemyDefenceMultiplier.set(Math.max(0.1f, enemyDefenceMultiplier.get() + effect.getEffectMultiplier()));
                        }
                    }
                }
            });
        }
    }

    private void attack(@NotNull Character character, @NotNull FightActionResponse response, @NotNull Fight fight) {
        var playerStatistics = character.getStatistics();

        var playerDamage = Math.round(character.getStatistics().getDamage() * playerDamageMultiplier.get() / enemyDefenceMultiplier.get());
        if (new Random().nextFloat(0, 100) < playerStatistics.getCriticalChance()) {
            playerDamage *= 2;
            response.setPlayerCriticalStrike(true);
        }
        fight.dealDamageToEnemy(playerDamage);
        response.setPlayerDamage(playerDamage);
        character.setStatistics(playerStatistics);
        character.getOccupation().setFight(fight);
    }

    private void usePotion(@NotNull Character character) {
        checkIfHpAlreadyFull(character.getStatistics());
        checkIfCharacterHasPotions(character.getEquipment());
        character.usePotion();
    }

    private void specialAttack(@NotNull Character character, @NotNull Fight fight, @NotNull FightActionResponse response, long skillId) {
        var skill = skillRepository.get(skillId);
        var statistics = character.getStatistics();
        var skillLevel = character.getSkills().stream().filter(s -> s.getSkill().getId().equals(skill.getId())).findFirst().orElseThrow(SkillNotFoundException::new).getLevel();
        if (statistics.getCurrentMana() < skill.getManaCost()) {
            throw new NotEnoughManaException();
        }
        if (skill.getEffect() != null) {
            var effects = Optional.ofNullable(fight.getFightEffects()).orElse(new HashSet<>()).stream().
                    filter(fightEffect -> fightEffect.getDuration() <= 0).collect(Collectors.toSet());
            FightEffect fightEffect = new FightEffect();
            if (!effects.isEmpty()) {
                fightEffect = effects.stream().findFirst().get();
            }
            fightEffect.setFight(fight);
            int effectDuration = skill.getFinalEffectDuration(skillLevel);
            fightEffect.setDuration(effectDuration);
            fightEffect.setPlayerEffect(false);
            fightEffect.setSkillEffect(skill.getEffect());
            fightEffect.setEffectMultiplier(skill.getFinalEffectMultiplier(skillLevel));
            fight.addFightEffect(fightEffect);
        }
        var baseDamage = skill.isMagicDamage() ? statistics.getMagicDamage() : statistics.getDamage();
        var playerDamage = Math.round(skill.getDamageMultiplier(skillLevel) * baseDamage / enemyDefenceMultiplier.get());
        statistics.useMana(skill.getManaCost());
        fight.dealDamageToEnemy(playerDamage);
        response.setPlayerDamage(playerDamage);
        response.setPlayerCurrentMana(statistics.getCurrentMana());
        character.setStatistics(statistics);
    }

    private void enemyTurn(@NotNull FightActionResponse response,
                           @NotNull Fight fight,
                           @NotNull Character character) {
        var enemy = Optional.ofNullable(fight.getEnemy()).orElseThrow();
        var playerStatistics = character.getStatistics();
        if (fight.getEnemyCurrentHp() > 0) {
            var enemyAction = decideEnemyAction(fight, character);
            response.setEnemyAction(enemyAction.getElementAction());
            switch (enemyAction.getElementAction()) {
                case NORMAL_ATTACK -> enemyNormalAttack(response, playerStatistics, enemy);
                case SPECIAL_ATTACK -> enemySpecialAttack(response, playerStatistics, enemy, fight);
                case USE_POTION -> enemyUsePotion(response, playerStatistics, enemy, fight);
            }
        }
        character.setStatistics(playerStatistics);
        character.getOccupation().setFight(fight);
    }

    private void enemyNormalAttack(@NotNull FightActionResponse response, @NotNull Statistics playerStatistics, @NotNull Enemy enemy) {
        var successfulHit = new Random().nextFloat(0, 100) > playerStatistics.getDodgeChance();
        response.setEnemyHit(false);
        response.setEnemyDamage(0);
        if (successfulHit) {
            var enemyDamage = Math.max(1, enemy.getDamage() - Math.round(playerStatistics.getArmor() * playerDefenceMultiplier.get()));
            playerStatistics.takeDamage(enemyDamage);
            response.setEnemyHit(true);
            response.setEnemyDamage(enemyDamage);
            response.setPlayerCurrentHp(playerStatistics.getCurrentHp());
        }
    }

    private void enemySpecialAttack(@NotNull FightActionResponse response, @NotNull Statistics playerStatistics, @NotNull Enemy enemy, @NotNull Fight fight) {
        var skill = enemy.getSkill();
        response.setEnemyHit(false);
        if (fight.getEnemyCurrentMana() < skill.getManaCost()) {
            enemyNormalAttack(response, playerStatistics, enemy);
        } else {
            var successfulHit = new Random().nextFloat(0, 100) > playerStatistics.getDodgeChance();
            if (successfulHit) {
                int effectDuration = skill.getEffectDuration();
                var enemyDamage = Math.max(1,
                        Math.round((enemy.getSkill().getMultiplier() + effectDuration * enemy.getSkillLevel()) *
                                enemy.getDamage() + enemy.getDamage() - playerStatistics.getArmor() * playerDefenceMultiplier.get()));
                playerStatistics.takeDamage(enemyDamage);
                fight.enemyUseMana();
                response.setEnemyDamage(enemyDamage);
                response.setEnemyHit(true);
                response.setPlayerCurrentHp(playerStatistics.getCurrentHp());
                if (skill.getEffect() != null) {
                    var effects = Optional.ofNullable(fight.getFightEffects()).orElse(new HashSet<>()).stream()
                            .filter(fightEffect -> fightEffect.getDuration() <= 0).collect(Collectors.toSet());
                    FightEffect fightEffect = new FightEffect();
                    if (!effects.isEmpty()) {
                        fightEffect = effects.stream().findFirst().get();
                    }
                    fightEffect.setFight(fight);
                    fightEffect.setDuration(effectDuration);
                    fightEffect.setPlayerEffect(true);
                    fightEffect.setSkillEffect(skill.getEffect());
                    fightEffect.setEffectMultiplier(skill.getFinalEffectMultiplier(enemy.getSkillLevel()));
                    fight.addFightEffect(fightEffect);
                }
            }
        }
    }

    private void enemyUsePotion(@NotNull FightActionResponse response, @NotNull Statistics playerStatistics, @NotNull Enemy enemy, @NotNull Fight fight) {
        if (enemy.getNumberOfPotions() > 0) {
            fight.healEnemy();
            enemy.usePotion();
        } else {
            enemyNormalAttack(response, playerStatistics, enemy);
        }
    }

    private StrategyElement decideEnemyAction(@NotNull Fight fight, @NotNull Character character) {
        var enemy = Optional.ofNullable(fight.getEnemy()).orElseThrow();
        StrategyElement enemyAction;
        var strategy = enemy.getStrategyElements();
        var enemyHpPercent = (float) fight.getEnemyCurrentHp() / (float) enemy.getHp() * 100;
        var playerHpPercent = (float) character.getStatistics().getCurrentHp() / (float) character.getStatistics().getMaxHp() * 100;
        if (enemyHpPercent < 20) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.ENEMY_HEALTH_0_20).findFirst().orElseThrow();
        } else if (playerHpPercent < 20) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.PLAYER_HEALTH_0_20).findFirst().orElseThrow();
        } else if (enemyHpPercent < 40) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.ENEMY_HEALTH_20_40).findFirst().orElseThrow();
        } else if (playerHpPercent < 40) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.PLAYER_HEALTH_20_40).findFirst().orElseThrow();
        } else if (enemyHpPercent < 60) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.ENEMY_HEALTH_40_60).findFirst().orElseThrow();
        } else if (playerHpPercent < 60) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.PLAYER_HEALTH_40_60).findFirst().orElseThrow();
        } else if (enemyHpPercent < 80) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.ENEMY_HEALTH_60_80).findFirst().orElseThrow();
        } else if (playerHpPercent < 80) {
            enemyAction = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.PLAYER_HEALTH_60_80).findFirst().orElseThrow();
        } else {
            var action = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.PLAYER_HEALTH_80_100).findFirst().orElseThrow();
            var action2 = strategy.stream().filter(s ->
                    s.getElementEvent() == ElementEvent.ENEMY_HEALTH_80_100).findFirst().orElseThrow();
            enemyAction = action.getPriority() > action2.getPriority() ? action : action2;
        }
        return enemyAction;
    }

    private void winFight(@NotNull Fight fight, @NotNull Character character, @NotNull FightActionResponse response) {
        fight.setActive(false);
        fight.setEnemy(null);
        var adventure = character.getOccupation().getAdventure();
        if (adventure == null) {
            throw new AdventureNotFoundException();
        }
        character.getEquipment().earnGold(adventure.getGoldForAdventure());
        character.getStatistics().earnXp(adventure.getXpForAdventure());
        character.getOccupation().setAdventure(null);
        character.getOccupation().setFight(fight);
        if (fight.getFightEffects() != null) {
            fight.getFightEffects().forEach(fightEffect -> fightEffect.setDuration(0));
        }
        response.setPlayerWon(true);
    }

    private void loseFight(@NotNull Fight fight, @NotNull Character character, @NotNull FightActionResponse response) {
        fight.setActive(false);
        fight.setEnemy(null);
        character.getOccupation().setAdventure(null);
        character.getOccupation().setFight(fight);
        if (fight.getFightEffects() != null) {
            fight.getFightEffects().forEach(fightEffect -> fightEffect.setDuration(0));
        }
        response.setPlayerWon(false);
    }

    private void checkIfFightIsActive(@NotNull Fight fight) {
        if (!fight.isActive()) {
            throw new FightIsNotActiveException();
        }
    }

    private void checkIfHpAlreadyFull(@NotNull Statistics statistics) {
        if (statistics.getCurrentHp() >= statistics.getMaxHp()) {
            throw new CharacterHpFullException();
        }
    }

    private void checkIfCharacterHasPotions(@NotNull Equipment equipment) {
        if (equipment.getHealthPotions() <= 0) {
            throw new NoPotionsLeftException();
        }
    }
}
