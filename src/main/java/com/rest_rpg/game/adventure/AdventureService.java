package com.rest_rpg.game.adventure;

import com.rest_rpg.game.adventure.model.Adventure;
import com.rest_rpg.game.adventure.model.AdventureCreateRequestDto;
import com.rest_rpg.game.character.CharacterRepository;
import com.rest_rpg.game.character.CharacterService;
import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.enemy.EnemyRepository;
import com.rest_rpg.game.enemy.model.Enemy;
import com.rest_rpg.game.exceptions.AdventureNameExistsException;
import com.rest_rpg.game.exceptions.AdventureNotFoundException;
import com.rest_rpg.game.exceptions.CharacterIsNotOnAdventureException;
import com.rest_rpg.game.exceptions.CharacterStillOnAdventureException;
import com.rest_rpg.game.exceptions.FightIsOngoingException;
import com.rest_rpg.game.fight.model.Fight;
import com.rest_rpg.game.helpers.SearchHelper;
import com.rest_rpg.user.feign.UserInternalClient;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.AdventureBasicPage;
import org.openapitools.model.AdventureCreateRequest;
import org.openapitools.model.AdventureDetails;
import org.openapitools.model.AdventureLite;
import org.openapitools.model.AdventureSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final EnemyRepository enemyRepository;
    private final CharacterRepository characterRepository;
    private final UserInternalClient userInternalClient;
    private final AdventureMapper mapper;

    @Transactional
    public AdventureLite createAdventure(@NotNull AdventureCreateRequest request) {
        AdventureCreateRequestDto dto = mapper.toDto(request);
        checkIfAdventureExists(dto.getName());
        Enemy enemy = enemyRepository.getById(dto.getEnemy());
        Adventure adventure = Adventure.of(dto, enemy);
        return mapper.toLite(adventureRepository.save(adventure));
    }

    @Transactional
    public AdventureLite editAdventure(long adventureId, @NotNull AdventureCreateRequest request) {
        AdventureCreateRequestDto dto = mapper.toDto(request);
        checkIfEditedAdventureExists(dto.getName(), adventureId);
        Enemy enemy = enemyRepository.getById(dto.getEnemy());
        Adventure adventure = adventureRepository.getAdventureById(adventureId);
        adventure.modify(dto, enemy);
        return mapper.toLite(adventureRepository.save(adventure));
    }

    @Transactional
    public AdventureLite deleteAdventure(long adventureId) {
        Adventure adventure = adventureRepository.getAdventureById(adventureId);
        adventure.setDeleted(true);
        return mapper.toLite(adventureRepository.save(adventure));
    }

    @Transactional
    public AdventureBasicPage findAdventures(@NotNull AdventureSearchRequest request) {
        Pageable pageable = SearchHelper.getPageable(request.getPagination());
        return mapper.toPage(adventureRepository.findAdventures(request, pageable));
    }

    @Transactional
    public AdventureDetails getAdventure(long adventureId) {
        return mapper.toDetails(adventureRepository.getAdventureById(adventureId));
    }

    @Transactional
    public AdventureLite startAdventure(long adventureId, long characterId) {
        Character character = characterRepository.getCharacterById(characterId);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        character.getOccupation().throwIfCharacterIsOccupied();

        Adventure adventure = adventureRepository.getAdventureById(adventureId);
        character.getOccupation().startAdventure(adventure);
        characterRepository.save(character);

        return mapper.toLite(adventure);
    }

    @Transactional
    public AdventureLite endAdventure(long characterId) {
        Character character = characterRepository.getCharacterById(characterId);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        if (Optional.ofNullable(character.getOccupation().getFinishTime()).orElseThrow(AdventureNotFoundException::new).isAfter(LocalDateTime.now())) {
            throw new CharacterStillOnAdventureException();
        }
        checkIfFightIsOngoing(character.getOccupation().getFight());
        Adventure adventure = Optional.ofNullable(character.getOccupation().getAdventure())
                .orElseThrow(CharacterIsNotOnAdventureException::new);

        character.getOccupation().endAdventure(adventure);
        characterRepository.save(character);

        return mapper.toLite(adventure);
    }

    private void checkIfAdventureExists(@NotBlank String adventureName) {
        if (adventureRepository.existsByNameIgnoreCaseAndDeletedFalse(adventureName)) {
            throw new AdventureNameExistsException();
        }
    }

    private void checkIfEditedAdventureExists(@NotBlank String adventureName, long adventureId) {
        Optional<Adventure> optionalAdventure = adventureRepository.findByNameIgnoreCaseAndDeletedFalse(adventureName);
        if (optionalAdventure.isPresent() && !optionalAdventure.get().getId().equals(adventureId)) {
            throw new AdventureNameExistsException();
        }
    }

    private void checkIfFightIsOngoing(@NotNull Fight fight) {
        if (fight.isActive()) {
            throw new FightIsOngoingException();
        }
    }
}
