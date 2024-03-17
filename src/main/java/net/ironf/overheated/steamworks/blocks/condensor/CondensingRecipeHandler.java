package net.ironf.overheated.steamworks.blocks.condensor;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.steamFluids.AllSteamFluids;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CondensingRecipeHandler implements ResourceManagerReloadListener {
    //Maps each fluid to its output considering stack size
    public static HashMap<FluidStack,FluidStack> condensingHandler = new HashMap<>();
    //Maps each fluid present to input needed to perform recipe
    public static HashMap<Fluid,Integer> condensingPresentList = new HashMap<>();
    public static Level level = null;
    public static void setLevel(Level level) {
        CondensingRecipeHandler.level = level;
    }
    public static void generateHandler(){
        if (level == null){
            return;
        }
        reloadHandler();
        Overheated.LOGGER.info("Generating Condensing Handler");
        for (int p = 1; p <= 4; p++){
            for (Fluid steam : AllSteamFluids.Steams[p]){
                condensingHandler.put(new FluidStack(steam,1),new FluidStack(AllSteamFluids.DISTILLED_WATER.get(),p));
                condensingPresentList.put(steam,1);
            }
        }
    }

    public static void reloadHandler(){
        if (level == null){
            return;
        }
        condensingHandler.clear();
        List<CondenserRecipe> recipeList = createRecipeCollection();
        for (CondenserRecipe r : recipeList){
            for (FluidStack f : r.getInput().getMatchingFluidStacks()){

                condensingHandler.put(f,r.getOutput());
                condensingPresentList.put(f.getFluid(),f.getAmount());
            }
        }
    }

    public static List<CondenserRecipe> createRecipeCollection(){
        return level.getRecipeManager().getAllRecipesFor(CondenserRecipe.Type.INSTANCE);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        reloadHandler();
    }
}
