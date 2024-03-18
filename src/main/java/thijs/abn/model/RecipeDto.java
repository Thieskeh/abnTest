package thijs.abn.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link thijs.abn.entity.Recipe}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description ="DTO for Recipe")
public record RecipeDto(
        @Schema(example = "Optional when creating a new recipe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("id") Long id, // Removed @NotNull and @PositiveOrZero for creation context

        @Schema(example = "Pizza Margherita", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("name") @NotNull @Size(min = 2, max = 30) @NotEmpty @NotBlank String name,
        @Schema(example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("isVegetarian") @NotNull boolean isVegetarian,
        @Schema(example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("servings") @Positive @NotNull int servings,

        @Schema(example = "Spread the tomato sauce on the dough and bake for 10 minutes.", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("instructions") @NotEmpty @NotBlank @NotNull String instructions,

        @Schema(example = "[{\"name\": \"Tomato Sauce\"}, {\"name\": \"Mozzarella Cheese\"}]", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("ingredients") Set<IngredientDto> ingredients) implements Serializable {
}