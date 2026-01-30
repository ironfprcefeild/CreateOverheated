package net.ironf.overheated.recipes.JEI;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.AllFluids;
import net.ironf.overheated.AllItems;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.colants.CoolantRecipe;
import net.ironf.overheated.cooling.colants.CoolingRecipeCategory;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserRecipe;
import net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Overheated.asResource("jei_plugin");
    }


    public static RecipeType<CoolantRecipe> COOLING_TYPE =
            new RecipeType<>(Overheated.asResource( "cooling"), CoolantRecipe.class);
    public static RecipeType<CondenserRecipe> CONDENSING_TYPE =
            new RecipeType<>(Overheated.asResource( "condensing"), CondenserRecipe.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CoolingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CondensingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));



    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<CoolantRecipe> recipesLaserCooling = rm.getAllRecipesFor(CoolantRecipe.Type.INSTANCE);
        registration.addRecipes(COOLING_TYPE, recipesLaserCooling);

        List<CondenserRecipe> recipesCondensing = rm.getAllRecipesFor(CondenserRecipe.Type.INSTANCE);
        registration.addRecipes(CONDENSING_TYPE, recipesCondensing);


    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.DIODE.get().asItem()), COOLING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.CONDENSER.get().asItem()), COOLING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.CONDENSER.get().asItem()), CONDENSING_TYPE);

    }



}
