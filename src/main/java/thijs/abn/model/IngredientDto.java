package thijs.abn.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * DTO for {@link thijs.abn.entity.Ingredient}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ingredient")
public record IngredientDto(
        @Schema(example = "Optional when creating a new ingredient", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("id") Long id, // Removed @NotNull and @PositiveOrZero for creation context

        @Schema(example = "Cinnamon", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("name") @NotNull @Size(min = 2, max = 30) @NotEmpty @NotBlank String name) implements Serializable {

}