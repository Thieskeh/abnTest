package thijs.abn;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import thijs.abn.model.RecipeDto;
import thijs.abn.repository.RecipeRepository;
import thijs.abn.service.RecipeService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class RecipeDbInit {

    private final RecipeRepository recipeRepository;
    private final ResourceLoader resourceLoader;
    private final RecipeService recipeService;
    private final ObjectMapper objectMapper;

    private final Logger log = LoggerFactory.getLogger(RecipeDbInit.class);

    String path = "classpath:recipes.json";

    @Autowired
    public RecipeDbInit(RecipeService recipeService, RecipeRepository recipeRepository, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
        try {
            log.info("Populating database...");
            if (recipeRepository.count() > 0) {
                log.info("Recipe library not empty, skipping the population.");
                return; // Database is already populated
            }

            // Load JSON from the file into List<RecipeDto>
            List<RecipeDto> recipesDto = objectMapper.readValue(Files.readAllBytes(Paths.get(resourceLoader.getResource(path).getURI())), new TypeReference<List<RecipeDto>>() {
            });

            // Create each recipe using the service method
            recipesDto.forEach(recipeService::createRecipe);

            System.out.println("Database populated successfully.");
        } catch (
                Exception e) {
            log.error("Error during database initialization", e);
        }
    }
}
