package thijs.abn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thijs.abn.model.RecipeDto;
import thijs.abn.service.RecipeService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/recipes")
@Tag(name = "recipe service", description = "the recipe endpoint")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @Operation(summary = "Create a new recipe", description = "Add a new recipe to the database including ingredients.")
    @ApiResponse(responseCode = "201", description = "Recipe created successfully")
    public ResponseEntity<Void> createRecipe(@RequestBody RecipeDto recipeDto) {
        recipeService.createRecipe(recipeDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/id")
    @Operation(summary = "Returns the id of a recipe by name", description = "Get the id of a recipe by name.")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Failure")
    public ResponseEntity<Long> getRecipeIdByName(@RequestParam String name) {
        Long recipeId = recipeService.getRecipeIdByName(name);
        return ResponseEntity.ok(recipeId);
    }

    @PutMapping("/{recipeId}")
    @Operation(summary = "Update a recipe", description = "Updates the specified recipe with new values provided in the request body.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The updated recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            })
    public ResponseEntity<RecipeDto> updateRecipe(
            @Parameter(description = "ID of the recipe to update", required = true) @PathVariable Long recipeId,
            @Parameter(description = "Updated recipe information", required = true) @RequestBody RecipeDto recipeDto) {

        RecipeDto updatedRecipeDto = recipeService.updateRecipe(recipeId, recipeDto);
        return ResponseEntity.ok(updatedRecipeDto);
    }

    @DeleteMapping("/{recipeId}")
    @Operation(summary = "Delete a recipe", description = "Deletes the specified recipe by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Recipe successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            })
    public ResponseEntity<Void> deleteRecipe(
            @Parameter(description = "ID of the recipe to delete", required = true) @PathVariable Long recipeId) {

        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    @Operation(summary = "Return all recipes", description = "Get all recipes.")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Failure")
    public List<RecipeDto> getAllRecipes() {
        return recipeService.findAllRecipes();
    }

    @GetMapping("/search")
    @Operation(summary = "Search recipes", description = "Filter available recipes based on one or more of the following criteria:\n" +
            "1. Whether or not the recipe is vegetarian\n" +
            "2. The number of servings\n" +
            "3. Specific ingredients (either include or exclude)\n" +
            "4. Text search within the instructions.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of recipes based on the given criteria")
            })
    public ResponseEntity<List<RecipeDto>> searchRecipes(
            @Parameter(description = "true for vegetarian recipes, false for non-vegetarian, null for both") @RequestParam(name = "isVegetarian", required = false) Boolean isVegetarian,
            @Parameter(description = "minimum number of servings") @RequestParam(name = "minServings", required = false) Integer minServings,
            @Parameter(description = "maximum number of servings") @RequestParam(name = "maxServings", required = false) Integer maxServings,
            @Parameter(description = "comma-separated list of ingredients to be included in the dish") @RequestParam(name = "includedIngredients", required = false) String includedIngredients,
            @Parameter(description = "comma-separated list of ingredients to be excluded from the dish") @RequestParam(name = "excludedIngredients", required = false) String excludedIngredients,
            @Parameter(description = "word to be found in the instructions") @RequestParam(name = "queryInstructions", required = false) String queryInstructions) {

        List<String> includedIngredientsList = includedIngredients != null ? strToList(includedIngredients) : null;
        List<String> excludedIngredientsList = excludedIngredients != null ? strToList(excludedIngredients) : null;

        List<RecipeDto> recipes = recipeService.searchRecipes(
                isVegetarian,
                minServings,
                maxServings,
                includedIngredientsList,
                excludedIngredientsList,
                queryInstructions
        );

        return ResponseEntity.ok(recipes);
    }

    private List<String> strToList(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }
        return Stream.of(data.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
