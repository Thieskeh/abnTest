package thijs.abn.repository;

import thijs.abn.entity.Ingredient;
import thijs.abn.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RecipeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    public void setUp() {
        // Initial setup if needed
    }

    @Test
    void testRecipeAndIngredientsPersistenceAndRetrieval() {
        // Setup Ingredients
        Ingredient salt = new Ingredient();
        salt.setName("Salt");
        Ingredient pepper = new Ingredient();
        pepper.setName("Pepper");

        // Persist Ingredients to manage them and generate IDs
        salt = entityManager.persistFlushFind(salt);
        pepper = entityManager.persistFlushFind(pepper);

        // Setup and Persist Recipe
        Recipe chiliConCarne = new Recipe();
        chiliConCarne.setName("Chili Con Carne");
        chiliConCarne.setVegetarian(false);
        chiliConCarne.setServings(4);
        chiliConCarne.setInstructions("Instructions for making Chili Con Carne.");

        Set<Ingredient> ingredients = new HashSet<>();
        ingredients.add(salt);
        ingredients.add(pepper);
        chiliConCarne.setIngredients(ingredients);

        chiliConCarne = entityManager.persistFlushFind(chiliConCarne);

        // Retrieve the persisted Recipe
        Recipe foundRecipe = entityManager.find(Recipe.class, chiliConCarne.getId());

        // Assertions
        assertNotNull(foundRecipe);
        assertEquals("Chili Con Carne", foundRecipe.getName());
        assertFalse(foundRecipe.isVegetarian());
        assertEquals(4, foundRecipe.getServings());
        assertEquals("Instructions for making Chili Con Carne.", foundRecipe.getInstructions());
        assertNotNull(foundRecipe.getIngredients());
        assertEquals(2, foundRecipe.getIngredients().size());

        assertTrue(foundRecipe.getIngredients().stream().anyMatch(ingredient -> "Salt".equals(ingredient.getName())));
        assertTrue(foundRecipe.getIngredients().stream().anyMatch(ingredient -> "Pepper".equals(ingredient.getName())));
    }

    @Test
    void testFindByName() {
        // Given
        String recipeName = "Chili Con Carne";
        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setVegetarian(false);
        recipe.setServings(4);
        recipe.setInstructions("Some instructions here.");

        Ingredient salt = new Ingredient();
        salt.setName("Salt");
        entityManager.persist(salt); // Assuming cascading is not configured for ingredients in Recipe entity

        Set<Ingredient> ingredients = new HashSet<>();
        ingredients.add(salt);
        recipe.setIngredients(ingredients);

        entityManager.persistAndFlush(recipe); // Persist and flush to ensure it's saved immediately for retrieval

        // When
        Optional<Recipe> foundRecipeOpt = recipeRepository.findByName(recipeName);

        // Then
        assertTrue(foundRecipeOpt.isPresent(), "Recipe should be found by name");

        foundRecipeOpt.ifPresent(foundRecipe -> {
            assertEquals(recipeName, foundRecipe.getName(), "Recipe names should match");
            assertEquals(4, foundRecipe.getServings(), "Servings should match");
            assertFalse(foundRecipe.isVegetarian(), "Vegetarian flag should match");
            assertNotNull(foundRecipe.getIngredients(), "Ingredients should not be null");
            assertEquals(1, foundRecipe.getIngredients().size(), "Ingredients size should match");
            assertTrue(foundRecipe.getIngredients().stream().anyMatch(ing -> "Salt".equals(ing.getName())), "Ingredient should contain 'Salt'");
        });
    }

    @Test
    void testDeleteRecipe() {
        // Assuming a recipe is already saved
        Recipe recipe = new Recipe();
        recipe.setName("Recipe to Delete");
        recipe.setVegetarian(false);
        recipe.setServings(1);
        recipe.setInstructions("Some deletable instructions.");
        entityManager.persistAndFlush(recipe);

        // Delete the recipe
        recipeRepository.delete(recipe);

        // Validate the recipe is deleted
        Optional<Recipe> deletedRecipe = recipeRepository.findById(recipe.getId());
        assertTrue(deletedRecipe.isEmpty(), "Recipe should be deleted and not found.");
    }

    @Test
    void testUpdateRecipe() {
        // Assuming a recipe is already saved
        Recipe recipe = new Recipe();
        recipe.setName("Old Recipe");
        recipe.setVegetarian(true);
        recipe.setServings(2);
        recipe.setInstructions("Some old instructions.");
        entityManager.persistAndFlush(recipe);

        // Fetch, update and save the recipe
        Recipe fetchedRecipe = recipeRepository.findById(recipe.getId()).orElseThrow();
        fetchedRecipe.setName("Updated Recipe");
        fetchedRecipe.setInstructions("Updated instructions.");
        recipeRepository.save(fetchedRecipe);

        // Validate updates
        Recipe updatedRecipe = recipeRepository.findById(recipe.getId()).orElseThrow();
        assertEquals("Updated Recipe", updatedRecipe.getName(), "Recipe name should be updated.");
        assertEquals("Updated instructions.", updatedRecipe.getInstructions(), "Recipe instructions should be updated.");
    }

    @Test
    void testSaveRecipe() {
        // Create a new recipe
        Recipe recipe = new Recipe();
        recipe.setName("New Recipe");
        recipe.setVegetarian(true);
        recipe.setServings(4);
        recipe.setInstructions("Some instructions here.");

        // Save the recipe
        Recipe savedRecipe = recipeRepository.save(recipe);

        // Validate saved recipe
        assertNotNull(savedRecipe.getId(), "Recipe should have an ID after being saved.");
        assertEquals("New Recipe", savedRecipe.getName(), "Recipe name should match the saved name.");
    }
}

