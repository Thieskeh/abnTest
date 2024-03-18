package thijs.abn.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import thijs.abn.model.IngredientDto;
import thijs.abn.model.RecipeDto;
import thijs.abn.service.RecipeService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    private RecipeDto recipe1;
    private RecipeDto recipe2;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        IngredientDto ingredientDto1 = new IngredientDto(1L, "Ingredient1");
        IngredientDto ingredientDto2 = new IngredientDto(2L, "Ingredient2");
        Set<IngredientDto> ingredients1 = Set.of(ingredientDto1);
        Set<IngredientDto> ingredients2 = Set.of(ingredientDto2);

        recipe1 = new RecipeDto(1L, "Pizza Margherita", true, 4, "Spread the tomato sauce on the dough and bake for 10 minutes.", ingredients1);
        recipe2 = new RecipeDto(2L, "Veggie Pizza", true, 4, "Spread the tomato sauce, add veggies on the dough and bake for 12 minutes.", ingredients2);
        recipes = Arrays.asList(recipe1, recipe2);
    }

    @Test
    void testCreateRecipe() throws Exception {
        doNothing().when(recipeService).createRecipe(Mockito.any(RecipeDto.class));

        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Recipe\", \"isVegetarian\": false, \"servings\": 4, \"instructions\": \"Test instructions\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllRecipes() throws Exception {
        given(recipeService.findAllRecipes()).willReturn(recipes);

        mockMvc.perform(get("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
        // Further JSON path assertions as needed
        ;

        verify(recipeService).findAllRecipes();
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void testSearchRecipes() throws Exception {
        given(recipeService.searchRecipes(null, null, null, null, null, null)).willReturn(recipes);

        mockMvc.perform(get("/api/v1/recipes/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(recipes.size()))) // Assuming 'recipes' list is not empty
                .andExpect(jsonPath("$[0].name").value(recipes.get(0).name()))
        // Add more assertions as needed to verify the structure and content of the returned JSON
        ;
    }

}
