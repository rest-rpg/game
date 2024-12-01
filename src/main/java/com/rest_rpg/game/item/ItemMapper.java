package com.rest_rpg.game.item;

import com.rest_rpg.game.item.model.Item;
import com.rest_rpg.game.item.model.ItemCreateRequestDto;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.openapitools.model.ItemCreateRequest;
import org.openapitools.model.ItemLite;
import org.openapitools.model.ItemLitePage;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemCreateRequestDto toDto(@NotNull ItemCreateRequest source);

    ItemLite toLite(@NotNull Item source);

    ItemLitePage toPage(@NotNull Page<Item> source);
}
