package thijs.abn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import thijs.abn.entity.Ingredient;
import thijs.abn.entity.Recipe;
import thijs.abn.repository.RecipeRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RecipeDbInitTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void testDatabaseInitialization() {
        assertTrue(recipeRepository.count() >= 2, "Database should be populated with at least two recipes");
    }

}