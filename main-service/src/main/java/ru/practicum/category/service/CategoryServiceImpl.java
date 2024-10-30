package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    /**
     * Метод добавления категории в базу данных с уровнем доступа Admin
     * @return NewCategoryDto
     */
    @Override
    public NewCategoryDto addCategory(NewCategoryDto dto) {
        log.info("Validating category dto: {}", dto);
        if (repository.existsByName(dto.getName())) {
            throw new AlreadyExistsException("Category with name " + dto.getName() + " already exists");
        }

        Category category = CategoryMapper.toCategory(dto);
        repository.save(category);
        log.info("Category saved: {}", category);

        return CategoryMapper.toNewCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategory(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        log.info("Get all categories with pagination from={}, size={}", from, size);

        Page<Category> categoryPage = repository.findAll(pageable);

        return CategoryMapper.toCategoryDtoList(categoryPage.getContent());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        log.info("Get category by id: {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Category with id " + id + " not found");
        }

        return repository.findById(id)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto dto) {
        log.info("Update category: {}", dto);
        if (!repository.existsById(dto.getId())) {
            throw new NotFoundException("Category with id " + dto.getId() + " not found");
        }
        if (repository.existsByName(dto.getName())) {
            throw new AlreadyExistsException("Category with name " + dto.getName() + " already exists");
        }

        Category category = CategoryMapper.toCategory(dto);
        repository.save(category);
        log.info("Category updated: {}", category);

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Delete category by id: {}", id);
        repository.deleteById(id);
    }
}
