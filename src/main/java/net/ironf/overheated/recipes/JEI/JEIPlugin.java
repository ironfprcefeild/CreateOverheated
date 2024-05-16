package net.ironf.overheated.recipes.JEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.colants.LaserCoolantRecipe;
import net.ironf.overheated.laserOptics.colants.LaserCoolingRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Overheated.MODID, "jei_plugin");
    }


    public static RecipeType<LaserCoolantRecipe> LASER_COOLING_TYPE =
            new RecipeType<>(new ResourceLocation(Overheated.MODID, "laser_cooling"), LaserCoolantRecipe.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new LaserCoolingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<LaserCoolantRecipe> recipesLaserCooling = rm.getAllRecipesFor(LaserCoolantRecipe.Type.INSTANCE);
        registration.addRecipes(LASER_COOLING_TYPE, recipesLaserCooling);


    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.DIODE.get().asItem()), LASER_COOLING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.CONDENSER.get().asItem()), LASER_COOLING_TYPE);

    }



}
