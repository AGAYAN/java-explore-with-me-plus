package ru.practicum.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                           @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Request to get categories with pagination from={}, size={}", from, size);
        return categoryService.getCategory(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        log.info("Request to get category by id: {}", id);
        return categoryService.getCategoryById(id);
    }
}
