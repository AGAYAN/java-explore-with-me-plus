package ru.practicum.category.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.*;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

@Transactional
@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repository;
  private final EventRepository eventRepository;

  /**
   * Adding new category to the DB by Admin
   */
  @Override
  public CategoryDto addCategory(NewCategoryDto dto) {
    log.info("Validating category dto: {}", dto);
    if (repository.existsByName(dto.getName())) {
      throw new AlreadyExistsException("Category with name " + dto.getName() + " already exists");
    }

    Category category = CategoryMapper.toCategory(dto);
    repository.save(category);
    log.info("Category saved: {}", category);

    return CategoryMapper.toCategoryDto(category);
  }

  /**
   * Retrieves all available categories.
   */
  @Override
  @Transactional(readOnly = true)
  public List<CategoryDto> getCategory(int from, int size) {

    Pageable pageable = PageRequest.of(from / size, size);

    log.info("Get all categories with pagination from={}, size={}", from, size);

    Page<Category> categoryPage = repository.findAll(pageable);

    return CategoryMapper.toCategoryDtoList(categoryPage.getContent());
  }

  /**
   * Retrieves Category information with specified ID.
   */
  @Override
  @Transactional(readOnly = true)
  public CategoryDto getCategoryById(Long id) {
    log.info("Get category by id: {}", id);

    return repository.findById(id)
        .map(CategoryMapper::toCategoryDto)
        .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
  }

  /**
   * Updates category name for a category with specified ID.
   */
  @Override
  public CategoryDto updateCategory(Long id, NewCategoryDto dto) {
    log.info("Update category: {}", dto);
    Category category = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    if (repository.existsByName(dto.getName()) && !category.getName().equals(dto.getName())) {
      log.warn("Failed to update category. Name '{}' already exists.", dto.getName());
      throw new AlreadyExistsException("Category name already exists.");
    }
    category.setName(dto.getName());
    repository.save(category);
    return CategoryMapper.toCategoryDto(category);
  }

  /**
   * Deletes category record from the DB, ensuring it is not related to any event.
   */
  @Override
  public void deleteCategory(Long id) {
    log.info("Delete category by id: {}", id);
    if (!repository.existsById(id)) {
      throw new NotFoundException("Category with id " + id + " not found");
    }
    if (eventRepository.existsByCategoryId(id)) {
      log.warn("Category with id {} is in use by an event and cannot be deleted.", id);
      throw new ConflictException("Cannot be deleted; it's in use by an event.");
    }
    repository.deleteById(id);
  }
}
