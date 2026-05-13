package net.ironf.overheated.cooling.colants;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.recipes.AllRecipes;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;

public class CoolingHandler implements ResourceManagerReloadListener {

    //Heat is the maximum amount of heat units a laser can handle when using this coolant
    public static HashMap<Fluid,Integer> heatHandler = new HashMap<>();
    public static HashMap<Fluid,Float> efficiencyHandler = new HashMap<>();
    public static HashMap<Fluid,Float> minTempHandler = new HashMap<>();


    public static Level level = null;
    public static void setLevel(Level level) {
        CoolingHandler.level = level;
    }
    public static void generateHandler(){
        if (level == null){
            return;
        }
        Overheated.LOGGER.info("SO: Generating Coolant Recipe Helper");
        heatHandler.clear();
        List<RecipeHolder<CoolantRecipe>> recipeList = createRecipeCollection();
        for (RecipeHolder<CoolantRecipe> r : recipeList){
            for (FluidStack f : r.value().getInputFluid().getStacks()){
                heatHandler.put(f.getFluid(),r.value().getHeat());
                efficiencyHandler.put(f.getFluid(),r.value().getEfficiency());
                minTempHandler.put(f.getFluid(),-Math.abs(r.value().getMinTemp()));
            }
        }
    }

    public static List<RecipeHolder<CoolantRecipe>> createRecipeCollection(){
        return level.getRecipeManager().getAllRecipesFor(AllRecipes.COOLANT.TYPE.get());
    }


    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {
        generateHandler();
    }
}
