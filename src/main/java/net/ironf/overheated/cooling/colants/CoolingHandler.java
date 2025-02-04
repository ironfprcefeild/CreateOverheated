package net.ironf.overheated.cooling.colants;

import net.ironf.overheated.Overheated;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

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
        Overheated.LOGGER.info("Generating Coolant Recipe Helper");
        heatHandler.clear();
        List<CoolantRecipe> recipeList = createRecipeCollection();
        for (CoolantRecipe r : recipeList){
            for (FluidStack f : r.getInput().getMatchingFluidStacks()){
                heatHandler.put(f.getFluid(),r.getHeat());
                efficiencyHandler.put(f.getFluid(),r.getEfficiency());
                minTempHandler.put(f.getFluid(),r.getMinTemp());
            }
        }
    }

    public static List<CoolantRecipe> createRecipeCollection(){
        return level.getRecipeManager().getAllRecipesFor(CoolantRecipe.Type.INSTANCE);
    }


    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {
        generateHandler();
    }
}
