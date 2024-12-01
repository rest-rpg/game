package com.rest_rpg.game.helpers;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;

public class SearchHelper {

    public static Pageable getPageable(@NotNull Pagination pagination) {
        Sort pageableSort = Sort.by("id");
        if (pagination.getSortOrder() != null && pagination.getSort() != null) {
            pageableSort = Sort.by(pagination.getSort());
            pageableSort = pagination.getSortOrder().equalsIgnoreCase("asc") ? pageableSort.ascending() : pageableSort.descending();
            pageableSort = pageableSort.and(Sort.by("id"));
        }
        return PageRequest.of(pagination.getPageNumber(), pagination.getElements(), pageableSort);
    }

    public static QSort getQSort(@NotNull Pagination pagination) {
        var sortId = new OrderSpecifier<>(Order.ASC, Expressions.stringPath("id"));

        if (pagination.getSort() != null) {
            var sortDirection = pagination.getSortOrder().equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;
            var sortPath = Expressions.stringPath(pagination.getSort());
            var sort = new OrderSpecifier<>(sortDirection, sortPath);
            return new QSort(sortId, sort);
        }
        return new QSort(sortId);
    }
}
