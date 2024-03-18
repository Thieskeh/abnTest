package thijs.abn.repository;

import thijs.abn.entity.Ingredient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

@DataJpaTest
public class IngredientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    public void testFindByName() {
        // Prepare the ingredient
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");

        // Persist and flush to ensure it's saved and assigned an ID
        Ingredient persistedIngredient = entityManager.persistAndFlush(ingredient);

        // Retrieve the ingredient by name
        Optional<Ingredient> foundIngredientName = ingredientRepository.findByName("Salt");

        // Assertions
        Assertions.assertTrue(foundIngredientName.isPresent(), "Ingredient should be found by name.");
        Assertions.assertEquals(persistedIngredient.getName(), foundIngredientName.get().getName(), "Ingredient name should match.");

        // Assert that the ID is set (i.e., not null and greater than 0)
        Assertions.assertNotNull(foundIngredientName.get().getId(), "Ingredient ID should not be null.");
        Assertions.assertTrue(foundIngredientName.get().getId() > 0, "Ingredient ID should be greater than 0.");
    }

    @Test
    public void testUpdateIngredient() {
        // Prepare and save an initial Ingredient
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Pepper");
        Ingredient persistedIngredient = entityManager.persistAndFlush(ingredient);

        // Change a property of the Ingredient
        persistedIngredient.setName("Black Pepper");
        ingredientRepository.save(persistedIngredient);

        // Retrieve the updated Ingredient
        Optional<Ingredient> updatedIngredientOpt = ingredientRepository.findById(persistedIngredient.getId());

        Assertions.assertTrue(updatedIngredientOpt.isPresent(), "Updated ingredient should exist.");
        Ingredient updatedIngredient = updatedIngredientOpt.get();
        Assertions.assertEquals("Black Pepper", updatedIngredient.getName(), "Ingredient name should be updated.");
    }

    @Test
    public void testDeleteIngredient() {
        // Prepare and save an Ingredient
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Sugar");
        Ingredient persistedIngredient = entityManager.persistAndFlush(ingredient);

        // Delete the Ingredient
        ingredientRepository.delete(persistedIngredient);

        // Assert it no longer exists
        Optional<Ingredient> foundIngredient = ingredientRepository.findById(persistedIngredient.getId());
        Assertions.assertFalse(foundIngredient.isPresent(), "Ingredient should be deleted and not found.");
    }
}
