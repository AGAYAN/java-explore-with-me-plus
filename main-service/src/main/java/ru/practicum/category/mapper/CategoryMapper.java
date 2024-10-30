package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;
import java.util.Objects;

@UtilityClass
@Slf4j
public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        log.info("Mapping category to categoryDto: {}", category);
        Objects.requireNonNull(category);
        return new CategoryDto()
                .setId(category.getId())
                .setName(category.getName());
    }

    public static Category toCategory(CategoryDto categoryDto) {
        log.info("Mapping categoryDto to category: {}", categoryDto);
        Objects.requireNonNull(categoryDto);
        return new Category()
                .setId(categoryDto.getId())
                .setName(categoryDto.getName());
    }

    public static Category toCategory(NewCategoryDto categoryDto) {
        log.info("Mapping newCategoryDto to category: {}", categoryDto);
        Objects.requireNonNull(categoryDto);
        return new Category()
                .setName(categoryDto.getName());
    }

    public static NewCategoryDto toNewCategoryDto(Category category) {
        log.info("Mapping category to newCategoryDto: {}", category);
        Objects.requireNonNull(category);
        return new NewCategoryDto()
                .setName(category.getName());
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        log.info("Mapping categories to categoryDtoList: {}", categories);
        Objects.requireNonNull(categories);
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }
}
