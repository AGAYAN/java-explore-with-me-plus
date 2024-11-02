import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.service.CategoryServiceImpl;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void addCategory_ShouldAddCategory_WhenNameIsUnique() {
        NewCategoryDto newCategory = new NewCategoryDto();
        newCategory.setName("New Category");
        Category category = new Category();
        category.setId(1L);
        category.setName("New Category");

        when(repository.existsByName(newCategory.getName())).thenReturn(false);
        when(repository.save(Mockito.any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.addCategory(newCategory);

        assertEquals("New Category", result.getName());
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    void addCategory_ShouldThrowException_WhenNameExists() {
        NewCategoryDto newCategory = new NewCategoryDto();
        newCategory.setName("Duplicate Category");

        when(repository.existsByName(newCategory.getName())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> categoryService.addCategory(newCategory));
    }

    @Test
    void getCategory_ShouldReturnCategoriesWithPagination() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> categories = Arrays.asList(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories);
        Pageable pageable = PageRequest.of(0, 2);

        when(repository.findAll(pageable)).thenReturn(categoryPage);

        List<CategoryDto> result = categoryService.getCategory(0, 2);

        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenIdExists() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Existing Category");

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategoryById(1L);

        assertEquals("Existing Category", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenIdNotFound() {
        when(repository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void updateCategory_ShouldUpdateCategory_WhenIdExistsAndNameIsUnique() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Updated Category");

        Category category = new Category();
        category.setId(1L);
        category.setName("Old Category");

        when(repository.existsByName(newCategoryDto.getName())).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.updateCategory(1L, newCategoryDto);

        assertEquals("Updated Category", result.getName());
        verify(repository, times(1)).save(category);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenNameExists() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Duplicate Category");
        Category category = new Category().setName("Category");

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.existsByName(newCategoryDto.getName())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> categoryService.updateCategory(1L, newCategoryDto));
    }

    @Test
    void updateCategory_ShouldThrowException_WhenIdNotFound() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Updated Category");

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(1L, newCategoryDto));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory_WhenIdExists() {
        when(repository.existsById(anyLong())).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenIdNotFound() {
        when(repository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1L));
    }
}