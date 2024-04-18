package net.ironf.overheated.steamworks.blocks.condensor;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;

public class CondensingRecipeHandler implements ResourceManagerReloadListener {
    //Maps each fluid to its output considering stack size
    public static HashMap<Fluid, FluidStack> condensingHandler = new HashMap<>();
    public static HashMap<Fluid, Float> condensingMinTempHandler = new HashMap<>();
    public static HashMap<Fluid, Float> condensingAddTempHandler = new HashMap<>();


    public static Level level = null;
    public static void setLevel(Level level) {
        CondensingRecipeHandler.level = level;
    }
    public static void generateHandler(){
        if (level == null){
            return;
        }
        Overheated.LOGGER.info("Generating Condensing Handler");
        condensingHandler.clear();
        condensingAddTempHandler.clear();
        condensingMinTempHandler.clear();
        List<CondenserRecipe> recipeList = createRecipeCollection();
        for (CondenserRecipe r : recipeList){
            for (FluidStack f : r.getInput().getMatchingFluidStacks()){
                condensingHandler.put(f.getFluid(),r.getOutput());
                condensingMinTempHandler.put(f.getFluid(),r.getMinTemp());
                condensingAddTempHandler.put(f.getFluid(),r.getAddTemp());
            }
        }
        for (int p = 1; p <= 4; p++){
            for (Fluid steam : AllSteamFluids.Steams[p]){
                condensingHandler.put(steam,new FluidStack(AllSteamFluids.DISTILLED_WATER.get(),p));
                condensingAddTempHandler.put(steam,16f);
                condensingMinTempHandler.put(steam,0f);
            }
        }
    }


    public static List<CondenserRecipe> createRecipeCollection(){
        return level.getRecipeManager().getAllRecipesFor(CondenserRecipe.Type.INSTANCE);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        generateHandler();
    }
}
