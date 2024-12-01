package com.rest_rpg.game.character;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.character.model.CharacterArtwork;
import com.rest_rpg.game.character.model.dto.CharacterCreateRequestDto;
import com.rest_rpg.game.exceptions.CharacterAlreadyExistsException;
import com.rest_rpg.game.exceptions.CharacterNotFoundException;
import com.rest_rpg.game.exceptions.EnumValueNotFoundException;
import com.rest_rpg.game.exceptions.GetImageException;
import com.rest_rpg.game.exceptions.ImageDoesNotExistException;
import com.rest_rpg.game.exceptions.NotEnoughSkillPointsException;
import com.rest_rpg.game.statistics.dto.StatisticsUpdateRequestDto;
import com.rest_rpg.user.api.model.UserLite;
import com.rest_rpg.user.feign.UserInternalClient;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.openapitools.model.CharacterBasics;
import org.openapitools.model.CharacterCreateRequest;
import org.openapitools.model.CharacterDetails;
import org.openapitools.model.CharacterLite;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        try {
            dto = characterMapper.toDto(request);
        } catch (IllegalArgumentException exception) {
            throw new EnumValueNotFoundException();
        }
        assertCharacterDoesNotExist(dto.getName());
        UserLite user = userInternalClient.getUserFromContext();
        Character character = Character.createCharacter(dto, user.id());
        assertCharacterStatisticsAreValid(dto.getStatistics(), character.getStatistics().getFreeStatisticPoints());
        character.getStatistics().addStatistics(dto.getStatistics(), dto.getRace());

        return characterMapper.toLite(characterRepository.save(character));
    }

    @Transactional
    public ResponseEntity<Resource> getCharacterFullArtwork(@NotNull String characterArtwork) {
        return getCharacterArtwork(characterArtwork, "public/avatars/full/");
    }

    @Transactional
    public ResponseEntity<Resource> getCharacterThumbnailArtwork(@NotNull String characterArtwork) {
        return getCharacterArtwork(characterArtwork, "public/avatars/thumbnail/");
    }

    @Transactional
    public List<String> getCharacterArtworkEnum() {
        return Arrays.stream(CharacterArtwork.values()).map(Objects::toString).collect(Collectors.toList());
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

    private ResponseEntity<Resource> getCharacterArtwork(@NotNull String characterArtwork, @NotNull String artworksPath) {
        if (!EnumUtils.isValidEnum(CharacterArtwork.class, characterArtwork)) {
            throw new ImageDoesNotExistException();
        }
        String inputFile = artworksPath + CharacterArtwork.valueOf(characterArtwork).getArtworkName();
        Path path = new File(inputFile).toPath();
        FileSystemResource resource = new FileSystemResource(path);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(Files.probeContentType(path));
        } catch (IOException e) {
            throw new GetImageException();
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
