package net.ironf.overheated.utility.data.dataGeneration.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.BlastFurnaceStatus;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class RecipeBuilders {

    public static FinishedRecipe getPouringRecipe(ResourceLocation id,
                                           FluidStack input, ItemStack itemInput,
                                           ItemStack output){
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject j) {
                JsonArray inputs = new JsonArray();
                inputs.add(Ingredient.of(itemInput).toJson());
                inputs.add(FluidIngredient.fromFluid(input.getFluid(),input.getAmount()).serialize());
                j.add("ingredients",inputs);

                JsonArray results = new JsonArray();
                results.add(Ingredient.of(output).toJson());
                j.add("results",results);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return AllRecipeTypes.FILLING.getSerializer();
            }

            @Override
            public @Nullable JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public @Nullable ResourceLocation getAdvancementId() {
                return null;
            }
        };
    }

    public static FinishedRecipe getMeltingRecipe(ResourceLocation id, ItemStack input, FluidStack[] outputs, BlastFurnaceStatus requirements, int duration){
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject j) {
                j.add("input",Ingredient.of(input).toJson());

                JsonArray results = new JsonArray();
                for (FluidStack fs : outputs){
                    results.add(FluidIngredient.fromFluid(fs.getFluid(),fs.getAmount()).serialize());
                }
                j.add("results",results);

                j.add("status",requirements.toJson());

                j.addProperty("duration",duration);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return AllRecipes.INDUSTRIAL_MELTING.get();
            }

            @Override
            public @Nullable JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public @Nullable ResourceLocation getAdvancementId() {
                return null;
            }
        };
    }

    public static FinishedRecipe getCondensingRecipe(ResourceLocation id, FluidStack input, FluidStack output, float minTemp, float addTemp, HeatData generatedHeat){
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject j) {
                j.add("input",FluidIngredient.fromFluid(input.getFluid(),input.getAmount()).serialize());
                j.add("output",FluidIngredient.fromFluid(output.getFluid(),output.getAmount()).serialize());

                j.addProperty("addTemp",addTemp);
                j.addProperty("minTemp",minTemp);
                int heatLevel = generatedHeat.getHeatLevelOfHighest();
                j.addProperty("outputHeatLevel",heatLevel);
                j.addProperty("outputHeat",generatedHeat.getHeatOfLevel(heatLevel));

            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return AllRecipes.CONDENSER.get();
            }

            @Override
            public @Nullable JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public @Nullable ResourceLocation getAdvancementId() {
                return null;
            }
        };
    }
}
