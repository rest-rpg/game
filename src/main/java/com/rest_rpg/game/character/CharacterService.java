package com.rest_rpg.game.character;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.character.model.dto.CharacterCreateRequestDto;
import com.rest_rpg.game.exceptions.CharacterAlreadyExistsException;
import com.rest_rpg.game.exceptions.CharacterNotFoundException;
import com.rest_rpg.game.exceptions.NotEnoughSkillPointsException;
import com.rest_rpg.game.statistics.dto.StatisticsUpdateRequestDto;
import com.rest_rpg.user.api.model.UserLite;
import com.rest_rpg.user.feign.UserInternalClient;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.CharacterBasics;
import org.openapitools.model.CharacterCreateRequest;
import org.openapitools.model.CharacterDetails;
import org.openapitools.model.CharacterLite;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final UserInternalClient userInternalClient;
    private final CharacterMapper characterMapper;

    @Transactional
    public CharacterLite createCharacter(@NotNull CharacterCreateRequest request) {
        CharacterCreateRequestDto dto;
        dto = characterMapper.toDto(request);
        assertCharacterDoesNotExist(dto.name());
        UserLite user = userInternalClient.getUserFromContext();
        Character character = Character.createCharacter(dto, user.id());
        assertCharacterStatisticsAreValid(dto.statistics(), character.getStatistics().getFreeStatisticPoints());
        character.getStatistics().addStatistics(dto.statistics(), dto.race());

        return characterMapper.toLite(characterRepository.save(character));
    }

    @Transactional
    public CharacterBasics getUserCharacters() {
        UserLite user = userInternalClient.getUserFromContext();
        return characterMapper.toBasics(characterRepository.findByUserId(user.id()), 1);
    }

    @Transactional
    public CharacterDetails getUserCharacter(long characterId) {
        Character character = characterRepository.getCharacterById(characterId);
        checkIfCharacterBelongsToUser(character.getUserId(),userInternalClient);

        return characterMapper.toDetails(character);
    }

    public static void checkIfCharacterBelongsToUser(long userId, @NotNull UserInternalClient userInternalClient) {
        if (!userInternalClient.doesCharacterBelongToUser(userId)) {
            throw new CharacterNotFoundException();
        }
    }

    private void assertCharacterDoesNotExist(@NotNull String name) {
        if (characterRepository.existsByNameIgnoreCase(name)) {
            throw new CharacterAlreadyExistsException();
        }
    }

    private void assertCharacterStatisticsAreValid(@NotNull StatisticsUpdateRequestDto dto, int freePoints) {
        var sum = dto.getConstitution() + dto.getDexterity() + dto.getStrength() + dto.getIntelligence();
        if (sum > freePoints) {
            throw new NotEnoughSkillPointsException();
        }
    }
}
