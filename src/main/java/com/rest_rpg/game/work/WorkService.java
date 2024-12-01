package com.rest_rpg.game.work;

import com.rest_rpg.game.character.CharacterRepository;
import com.rest_rpg.game.character.CharacterService;
import com.rest_rpg.game.exceptions.CharacterStillWorkingException;
import com.rest_rpg.game.exceptions.WorkAlreadyExistsException;
import com.rest_rpg.game.exceptions.WorkNotFoundException;
import com.rest_rpg.game.helpers.SearchHelper;
import com.rest_rpg.game.work.model.Work;
import com.rest_rpg.user.feign.UserInternalClient;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.WorkCreateRequest;
import org.openapitools.model.WorkLite;
import org.openapitools.model.WorkLitePage;
import org.openapitools.model.WorkSearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class WorkService {

    private final WorkRepository workRepository;
    private final CharacterRepository characterRepository;
    private final UserInternalClient userInternalClient;
    private final WorkMapper workMapper;

    @Transactional
    public WorkLite createWork(@NotNull WorkCreateRequest request) {
        var dto = workMapper.toDto(request);
        checkIfWorkExists(dto.getName());
        var work = workRepository.save(Work.of(dto));
        return workMapper.toLite(work);
    }

    @Transactional
    public WorkLitePage findWorks(@NotNull WorkSearchRequest request) {
        var pageable = SearchHelper.getPageable(request.getPagination());
        return workMapper.toPage(workRepository.findWorks(request, pageable));
    }

    @Transactional
    public void startWork(long workId, long characterId) {
        var character = characterRepository.getCharacterById(characterId);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        character.getOccupation().throwIfCharacterIsOccupied();

        var work = workRepository.getWorkById(workId);
        character.getOccupation().startWork(work);
        characterRepository.save(character);
    }

    @Transactional
    public void endWork(long characterId) {
        var character = characterRepository.getCharacterById(characterId);
        CharacterService.checkIfCharacterBelongsToUser(character.getUserId(), userInternalClient);
        if (Optional.ofNullable(character.getOccupation().getFinishTime()).orElseThrow(WorkNotFoundException::new).isAfter(LocalDateTime.now())) {
            throw new CharacterStillWorkingException();
        }
        var work = Optional.ofNullable(character.getOccupation().getWork())
                .orElseThrow(WorkNotFoundException::new);

        character.getOccupation().endWork(work);
        characterRepository.save(character);
    }

    private void checkIfWorkExists(@NotBlank String workName) {
        if (workRepository.existsByNameIgnoreCase(workName)) {
            throw new WorkAlreadyExistsException();
        }
    }
}
