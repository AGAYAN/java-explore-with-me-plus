package ru.practicum.category.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

@Transactional(readOnly = true)
public interface CategoryService {

    @Transactional
    NewCategoryDto addCategory(NewCategoryDto dto);

    List<CategoryDto> getCategory(int from, int size);

    CategoryDto getCategoryById(Long id);

    @Transactional
    CategoryDto updateCategory(CategoryDto dto);

    @Transactional
    void deleteCategory(Long id);
}
