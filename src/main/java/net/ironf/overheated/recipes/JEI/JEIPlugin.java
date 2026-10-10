package net.ironf.overheated.recipes.JEI;

import com.mojang.datafixers.TypeRewriteRule;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.colants.CoolantRecipe;
import net.ironf.overheated.cooling.colants.CoolingRecipeCategory;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserRecipe;
import net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Overheated.asResource("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CoolingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CondensingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));



    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        AllRecipes.COOLANT.JEIRegisterRecipes(rm,registration);
        AllRecipes.CONDENSING.JEIRegisterRecipes(rm,registration);

    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.DIODE.get().asItem()), AllRecipes.COOLANT.JEIType);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.CONDENSER.get().asItem()), AllRecipes.COOLANT.JEIType);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.CONDENSER.get().asItem()), AllRecipes.CONDENSING.JEIType);
    }




}
