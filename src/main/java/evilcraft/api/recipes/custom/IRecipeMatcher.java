package evilcraft.api.recipes.custom;

import java.util.List;

/**
 * Helper class to abstract away some function logic (lambdas would make this way simpler though).
 * It allows you to define a matching criteria and allows you to filter out recipe that match these criteria and those that do not.
 * You do this by implementing the matches() method, which should return true when the criteria of the given recipe are met, or false otherwise.
 * @author rubensworks
 * @param <M> The machine type.
 * @param <R> The recipe type.
 */
@SuppressWarnings("rawtypes")
public interface IRecipeMatcher<M extends IMachine, R extends IRecipe> {

    /**
     * If the given recipe can be created in the given machine.
     * @param machine The machine.
     * @param recipeToMatch The recipe to match.
     * @return If it is valid for this machine.
     */
    public boolean matches(M machine, R recipeToMatch);

    /**
     * Find the recipe.
     * @return The recipe match.
     */
    public IRecipeMatch<M, R> findRecipe();

    /**
     * Find all the recipes.
     * @return All the matches.
     */
    public List<IRecipeMatch<M, R>> findRecipes();
}