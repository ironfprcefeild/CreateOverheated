package net.ironf.overheated.laserOptics.colants;

import net.ironf.overheated.Overheated;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;

public class LaserCoolingHandler implements ResourceManagerReloadListener {

    public static HashMap<Fluid,Integer> heatHandler = new HashMap<>();
    public static HashMap<Fluid,Integer> volatilityHandler = new HashMap<>();

    public static Level level = null;
    public static void setLevel(Level level) {
        LaserCoolingHandler.level = level;
    }
    public static void generateHandler(){
        if (level == null){
            return;
        }
        Overheated.LOGGER.info("Generating Laser Coolant Recipe Helper");
        List<LaserCoolantRecipe> recipeList = createRecipeCollection();
        for (LaserCoolantRecipe r : recipeList){
            for (FluidStack f : r.getInput().getMatchingFluidStacks()){
                heatHandler.put(f.getFluid(),r.getHeat());
                volatilityHandler.put(f.getFluid(),r.getVolatility());
            }
        }
    }

    public static List<LaserCoolantRecipe> createRecipeCollection(){
        return level.getRecipeManager().getAllRecipesFor(LaserCoolantRecipe.Type.INSTANCE);
    }


    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {
        generateHandler();
    }
}
