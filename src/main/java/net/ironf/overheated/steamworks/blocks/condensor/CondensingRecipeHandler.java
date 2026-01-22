package net.ironf.overheated.steamworks.blocks.condensor;

import net.ironf.overheated.Overheated;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;

public class CondensingRecipeHandler implements ResourceManagerReloadListener {
    //Maps each fluid to its output considering stack size
    public static HashMap<Fluid, CondensingOutputBundle> condensingHandler = new HashMap<>();

    public static Level level = null;
    public static void setLevel(Level level) {
        CondensingRecipeHandler.level = level;
    }
    public static void generateHandler(){
        if (level == null){
            return;
        }
        Overheated.LOGGER.info("SO: Generating Condensing Handler");
        condensingHandler.clear();
        List<CondenserRecipe> recipeList = createRecipeCollection();
        for (CondenserRecipe r : recipeList){
            for (FluidStack f : r.getInput().getMatchingFluidStacks()){
                condensingHandler.put(f.getFluid(),new CondensingOutputBundle(r.getOutput(),r.getMinTemp(),r.getAddTemp(),r.getGeneratedHeat()));
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
