package thijs.abn.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import thijs.abn.entity.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

    // Custom method to fetch all recipes along with their ingredients
    @Query("SELECT r FROM Recipe r JOIN FETCH r.ingredients")
    List<Recipe> findAllRecipesWithIngredients();

    Optional<Recipe> findByName(String name);

}
