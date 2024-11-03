import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.MainServiceApp;
import ru.practicum.category.controller.AdminCategoryController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminCategoryController.class)
@ContextConfiguration(classes = MainServiceApp.class)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCategory_ShouldReturnCategoryDto_WhenCategoryIsAdded() throws Exception {
        NewCategoryDto newCategory = new NewCategoryDto();
        newCategory.setName("New Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("New Category");

        Mockito.when(categoryService.addCategory(any(NewCategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    void addCategory_ShouldReturnConflict_WhenCategoryNameExists() throws Exception {
        NewCategoryDto newCategory = new NewCategoryDto();
        newCategory.setName("Existing Category");

        Mockito.when(categoryService.addCategory(any(NewCategoryDto.class)))
                .thenThrow(new AlreadyExistsException("Category with name Existing Category already exists"));

        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCategory_ShouldReturnCategoryDto_WhenCategoryIsUpdated() throws Exception {
        NewCategoryDto updatedCategory = new NewCategoryDto();
        updatedCategory.setName("Updated Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Updated Category");

        Mockito.when(categoryService.updateCategory(anyLong(), any(NewCategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(patch("/admin/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    void deleteCategory_ShouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Category not found"))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNotFound());
    }
}
