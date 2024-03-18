package thijs.abn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import thijs.abn.controller.RecipeController;
import thijs.abn.entity.Ingredient;
import thijs.abn.entity.Recipe;
import thijs.abn.repository.IngredientRepository;
import thijs.abn.repository.RecipeRepository;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class RecipeIntegrationTest {


    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    public void testIntegration() {

        Recipe recipe = getRecipe();
        ResponseEntity<Long> chiliConCarne = recipeController.getRecipeIdByName(recipe.getName().toLowerCase());


        assertTrue(chiliConCarne.getStatusCode().is2xxSuccessful());


    }

    @Transactional
    protected Recipe getRecipe() {
        // Setup Ingredients
        Ingredient salt = new Ingredient();
        salt.setName("salt".toLowerCase());
        Ingredient pepper = new Ingredient();
        pepper.setName("pepper".toLowerCase());

        // Persist Ingredients to manage them and generate IDs
        salt = ingredientRepository.save(salt);
        pepper = ingredientRepository.save(pepper);

        // Setup and Persist Recipe
        Recipe chiliConCarne = new Recipe();
        chiliConCarne.setName("Chili Con Carne".toLowerCase());
        chiliConCarne.setVegetarian(false);
        chiliConCarne.setServings(4);
        chiliConCarne.setInstructions("Instructions for making Chili Con Carne.");

        Set<Ingredient> ingredients = new HashSet<>();
        ingredients.add(salt);
        ingredients.add(pepper);
        chiliConCarne.setIngredients(ingredients);

        return recipeRepository.save(chiliConCarne);
    }
}
