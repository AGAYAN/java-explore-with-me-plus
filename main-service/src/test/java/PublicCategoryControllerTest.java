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
import ru.practicum.category.controller.PublicCategoryController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PublicCategoryController.class)
@ContextConfiguration(classes = MainServiceApp.class)
class PublicCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void getCategories_ShouldReturnListOfCategories() throws Exception {
        CategoryDto category1 = new CategoryDto();
        category1.setId(1L);
        category1.setName("Category 1");

        CategoryDto category2 = new CategoryDto();
        category2.setId(2L);
        category2.setName("Category 2");

        List<CategoryDto> categories = Arrays.asList(category1, category2);

        Mockito.when(categoryService.getCategory(0, 10)).thenReturn(categories);

        mockMvc.perform(get("/categories?from=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(category1.getId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[1].id").value(category2.getId()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()));
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenIdExists() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Category 1");

        Mockito.when(categoryService.getCategoryById(anyLong())).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        Mockito.when(categoryService.getCategoryById(anyLong()))
                .thenThrow(new NotFoundException("Category not found"));

        mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
