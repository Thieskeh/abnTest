package thijs.abn.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import thijs.abn.entity.Recipe;
import thijs.abn.mapper.RecipeMapper;
import thijs.abn.model.RecipeDto;
import thijs.abn.repository.IngredientRepository;
import thijs.abn.repository.RecipeRepository;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void findAllRecipes_ReturnsAllRecipes() {
        // Setup
        Recipe recipe = new Recipe();
        recipe.setName("Sample Recipe");
        List<Recipe> recipes = List.of(recipe);
        RecipeDto recipeDto = new RecipeDto(null, "Sample Recipe", false, 4, "Instructions", Set.of());

        Mockito.when(recipeRepository.findAllRecipesWithIngredients()).thenReturn(recipes);
        Mockito.when(recipeMapper.map(recipe)).thenReturn(recipeDto);

        // Execute
        List<RecipeDto> result = recipeService.findAllRecipes();

        // Verify
        Mockito.verify(recipeRepository).findAllRecipesWithIngredients();
        Mockito.verify(recipeMapper).map(recipe);

        // Assert
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Sample Recipe", result.get(0).name());
    }

}
