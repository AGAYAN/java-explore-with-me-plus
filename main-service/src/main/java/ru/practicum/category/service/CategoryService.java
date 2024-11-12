package ru.practicum.category.service;

import java.util.List;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    List<CategoryDto> getCategory(int from, int size);

    CategoryDto getCategoryById(Long id);

    CategoryDto updateCategory(Long id, NewCategoryDto dto);

    void deleteCategory(Long id);
}
