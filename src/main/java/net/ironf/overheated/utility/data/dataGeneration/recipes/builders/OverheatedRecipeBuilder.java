package net.ironf.overheated.utility.data.dataGeneration.recipes.builders;

import com.mojang.datafixers.types.Func;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.ironf.overheated.recipes.AllRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class OverheatedRecipeBuilder<T extends Recipe<?>> extends SimpleRecipeBuilder{
    public final T recipe;

    public OverheatedRecipeBuilder(T recipe) {
        this.recipe = recipe;
    }

    @Override
    public Item getResult() {
        return null;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation key) {
        // Build the advancement.
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
        output.accept(key, recipe, advancement.build(key.withPrefix("recipes/")));
    }
}
