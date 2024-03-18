package thijs.abn.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import thijs.abn.entity.Recipe;
import thijs.abn.model.RecipeDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface RecipeMapper {
    @Mapping(target = "vegetarian", source = "isVegetarian")
    Recipe map(RecipeDto recipeDto);

    @InheritInverseConfiguration
    RecipeDto map(Recipe recipe);
}
