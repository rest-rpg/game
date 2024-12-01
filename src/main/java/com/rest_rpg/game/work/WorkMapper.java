package com.rest_rpg.game.work;

import com.rest_rpg.game.work.model.Work;
import com.rest_rpg.game.work.model.WorkCreateRequestDto;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.openapitools.model.WorkCreateRequest;
import org.openapitools.model.WorkLite;
import org.openapitools.model.WorkLitePage;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface WorkMapper {

    WorkCreateRequestDto toDto(@NotNull WorkCreateRequest source);

    WorkLite toLite(@NotNull Work work);

    WorkLitePage toPage(@NotNull Page<Work> source);
}
