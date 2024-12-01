package com.rest_rpg.game.skill;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.SkillApi;
import org.openapitools.model.CharacterSkillBasics;
import org.openapitools.model.SkillBasicPage;
import org.openapitools.model.SkillCreateRequest;
import org.openapitools.model.SkillDetails;
import org.openapitools.model.SkillLite;
import org.openapitools.model.SkillLites;
import org.openapitools.model.SkillSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class SkillController implements SkillApi {

    private final SkillService skillService;

    @Override
    public ResponseEntity<SkillLite> createSkill(SkillCreateRequest skillCreateRequest) {
        return ResponseEntity.ok(skillService.createSkill(skillCreateRequest));
    }

    @Override
    public ResponseEntity<SkillBasicPage> findSkills(SkillSearchRequest skillSearchRequest) {
        return ResponseEntity.ok(skillService.findSkills(skillSearchRequest));
    }

    @Override
    public ResponseEntity<SkillDetails> getSkill(Long skillId) {
        return ResponseEntity.ok(skillService.getSkill(skillId));
    }

    @Override
    public ResponseEntity<SkillLites> getSkills() {
        return ResponseEntity.ok(skillService.getSkills());
    }

    @Override
    public ResponseEntity<CharacterSkillBasics> getCharacterSkills(Long characterId) {
        return ResponseEntity.ok(skillService.getCharacterSkills(characterId));
    }

    @Override
    public ResponseEntity<SkillLite> learnSkill(Long skillId, Long characterId) {
        return ResponseEntity.ok(skillService.learnSkill(skillId, characterId));
    }
}
