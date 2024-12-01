package com.rest_rpg.game.skill;

import com.rest_rpg.game.character.CharacterRepository;
import com.rest_rpg.game.character.CharacterService;
import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.exceptions.NotEnoughGoldException;
import com.rest_rpg.game.exceptions.NotEnoughSkillPointsException;
import com.rest_rpg.game.exceptions.SkillAlreadyExistsException;
import com.rest_rpg.game.exceptions.SkillCharacterClassMismatchException;
import com.rest_rpg.game.helpers.SearchHelper;
import com.rest_rpg.game.skill.model.Skill;
import com.rest_rpg.user.feign.UserInternalClient;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.CharacterSkillBasics;
import org.openapitools.model.SkillBasicPage;
import org.openapitools.model.SkillCreateRequest;
import org.openapitools.model.SkillDetails;
import org.openapitools.model.SkillLite;
import org.openapitools.model.SkillLites;
import org.openapitools.model.SkillSearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class SkillService {

    private final SkillRepository skillRepository;
    private final CharacterRepository characterRepository;
    private final UserInternalClient userInternalClient;
    private final SkillMapper skillMapper;

    @Transactional
    public SkillLite createSkill(@NotNull SkillCreateRequest skillCreateRequest) {
        var dto = skillMapper.toDto(skillCreateRequest);
        checkIfSkillExists(dto.getName());
        var skill = Skill.of(dto);
        return skillMapper.toLite(skillRepository.save(skill));
    }

    @Transactional
    public SkillBasicPage findSkills(@NotNull SkillSearchRequest request) {
        var pageable = SearchHelper.getPageable(request.getPagination());
        return skillMapper.toPage(skillRepository.findSkills(request, pageable));
    }

    public SkillDetails getSkill(long skillId) {
        return skillMapper.toDetails(skillRepository.get(skillId));
    }

    public SkillLites getSkills() {
        return skillMapper.toLites(skillRepository.findByDeletedFalse());
    }

    public CharacterSkillBasics getCharacterSkills(long characterId) {
        var character = characterRepository.getWithEntityGraph(characterId, Character.CHARACTER_SKILLS);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);

        return skillMapper.toCharacterSkillBasics(character);
    }

    @Transactional
    public SkillLite learnSkill(long skillId, long characterId) {
        var character = characterRepository.getWithEntityGraph(characterId, Character.CHARACTER_SKILLS);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        character.getOccupation().throwIfCharacterIsOccupied();
        var skill = skillRepository.get(skillId);
        validateSkillLearning(character, skill);
        character.learnNewSkill(skill);
        characterRepository.save(character);
        return skillMapper.toLite(skill);
    }

    private void checkIfSkillExists(@NotBlank String skillName) {
        if (skillRepository.existsByNameIgnoreCase(skillName)) {
            throw new SkillAlreadyExistsException();
        }
    }

    private void validateSkillLearning(@NotNull Character character, @NotNull Skill skill) {
        if (!skill.getCharacterClass().equals(character.getCharacterClass())) {
            throw new SkillCharacterClassMismatchException();
        }
        if (character.getEquipment().getGold() < skill.getSkillTraining().getGoldCost()) {
            throw new NotEnoughGoldException();
        }
        if (character.getStatistics().getFreeStatisticPoints() < skill.getSkillTraining().getStatisticPointsCost()) {
            throw new NotEnoughSkillPointsException();
        }
    }
}
