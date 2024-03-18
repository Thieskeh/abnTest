package thijs.abn.service;

import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thijs.abn.entity.Ingredient;
import thijs.abn.entity.Recipe;
import thijs.abn.exception.ResourceNotFoundException;
import thijs.abn.mapper.RecipeMapper;
import thijs.abn.model.RecipeDto;
import thijs.abn.repository.IngredientRepository;
import thijs.abn.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;

    private final Logger log = LoggerFactory.getLogger(RecipeService.class);

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                         RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.ingredientRepository = ingredientRepository;
    }

    public List<RecipeDto> findAllRecipes() {
        List<RecipeDto> recipeDtoList = recipeRepository.findAllRecipesWithIngredients()
                .stream()
                .map(recipeMapper::map)
                .collect(Collectors.toList());
        log.debug("RecipeDtoList: " + recipeDtoList);
        return recipeDtoList;
    }

    public Long getRecipeIdByName(String name) {
        return recipeRepository.findByName(name.toLowerCase())
                .map(Recipe::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with name: " + name));
    }

    @Transactional
    public void createRecipe(RecipeDto recipeDto) {
        Recipe recipe = recipeMapper.map(recipeDto);

        // Handle ingredients
        Set<Ingredient> ingredients = recipeDto.ingredients().stream()
                .map(dto -> {
                    String ingredientNameLowercase = dto.name().toLowerCase();
                    return ingredientRepository.findByName(ingredientNameLowercase)
                            .orElseGet(() -> {
                                Ingredient newIngredient = new Ingredient();
                                newIngredient.setName(ingredientNameLowercase);
                                return ingredientRepository.save(newIngredient); // Ensure the new ingredient is saved
                            });
                })
                .collect(Collectors.toSet());

        // Set ingredients to recipe and save
        recipe.setIngredients(ingredients);

        // Save recipe (which also saves ingredient relationships due to cascade settings)
        recipeRepository.save(recipe);
    }

    public List<RecipeDto> searchRecipes(Boolean isVegetarian, Integer minServings, Integer maxServings,
                                         List<String> includedIngredients, List<String> excludedIngredients,
                                         String queryInstructions) {
        Specification<Recipe> spec = createSpecification(isVegetarian, minServings, maxServings, includedIngredients, excludedIngredients, queryInstructions);
        List<Recipe> recipes = recipeRepository.findAll(spec);
        return recipes.stream()
                .map(recipeMapper::map)
                .collect(Collectors.toList());
    }

    private Specification<Recipe> createSpecification(Boolean isVegetarian, Integer minServings, Integer maxServings,
                                                      List<String> includedIngredients, List<String> excludedIngredients,
                                                      String queryInstructions) {
        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (isVegetarian != null) {
                predicates.add(criteriaBuilder.equal(root.get("isVegetarian"), isVegetarian));
            }

            if (minServings != null && maxServings != null) {
                predicates.add(criteriaBuilder.between(root.get("servings"), minServings, maxServings));
            } else if (minServings != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("servings"), minServings));
            } else if (maxServings != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("servings"), maxServings));
            }

            if (includedIngredients != null && !includedIngredients.isEmpty()) {
                // Convert all included ingredients to lowercase for case-insensitive comparison
                List<String> lowercaseIncludedIngredients = includedIngredients.stream().map(String::toLowerCase).collect(Collectors.toList());
                // Join with ingredients and perform a case-insensitive check
                predicates.add(root.join("ingredients").get("name").as(String.class).in(lowercaseIncludedIngredients));
            }

            if (excludedIngredients != null && !excludedIngredients.isEmpty()) {
                List<String> lowercaseExcludedIngredients = excludedIngredients.stream().map(String::toLowerCase).collect(Collectors.toList());

                Subquery<Recipe> excludedRecipesSubquery = query.subquery(Recipe.class);
                Root<Recipe> subqueryRoot = excludedRecipesSubquery.from(Recipe.class);
                Join<Recipe, Ingredient> excludedIngredientsJoin = subqueryRoot.join("ingredients");

                excludedRecipesSubquery.select(subqueryRoot)
                        .where(excludedIngredientsJoin.get("name").as(String.class).in(lowercaseExcludedIngredients));

                // Ensure the main query excludes recipes that match the subquery
                predicates.add(criteriaBuilder.not(root.in(excludedRecipesSubquery)));
            }

            if (queryInstructions != null && !queryInstructions.isBlank()) {
                // Ensure the explicit casting of the 'instructions' attribute to String.
                Expression<String> instructions = root.get("instructions").as(String.class);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(instructions), "%" + queryInstructions.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional
    public RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        // Update recipe properties
        recipe.setName(recipeDto.name());
        recipe.setVegetarian(recipeDto.isVegetarian());
        recipe.setServings(recipeDto.servings());
        recipe.setInstructions(recipeDto.instructions());

        // Existing ingredients in the recipe
        Set<String> existingIngredientNames = recipe.getIngredients().stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet());

        // DTO ingredients that need to be added or already exist in the recipe
        Set<Ingredient> updatedIngredients = recipeDto.ingredients().stream()
                .map(dto -> dto.name().toLowerCase())
                .distinct()
                .map(name -> ingredientRepository.findByName(name)
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setName(name);
                            return ingredientRepository.save(newIngredient);
                        }))
                .collect(Collectors.toSet());

        // Determine ingredients to remove based on the updated list
        recipe.getIngredients().removeIf(ingredient -> !updatedIngredients.contains(ingredient));

        // Add or retain ingredients from the DTO
        recipe.setIngredients(updatedIngredients);

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return recipeMapper.map(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        recipeRepository.delete(recipe);
    }

}

