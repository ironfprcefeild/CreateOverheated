package net.ironf.overheated.metalWorking;

import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemperatureTransformHandler implements ResourceManagerReloadListener {
    public static HashMap<UniversalState, ThermalInformationBundle> PhaseTransitionMap = new HashMap<>();

    public static Level level = null;
    public static void setLevel(Level level) {
        TemperatureTransformHandler.level = level;
    }
    public static void generateHandlers() {
        if (level == null){
            return;
        }
        Overheated.LOGGER.info("CO: Generating Phase Transition Handler");
        PhaseTransitionMap.clear();
        for (RecipeHolder<TemperatureTransformRecipe> RH : createRecipeCollection()){
            TemperatureTransformRecipe r = RH.value();
            PhaseTransformBundle pBundle = new PhaseTransformBundle(r.criticalPoints,r.phases);
            //BlockStates affected by this phase transition recipe
            for (int i = 0; i < pBundle.createdStates.size(); i++) {
                UniversalState phaseState = pBundle.createdStates.get(i);

                PhaseTransitionMap.put(phaseState, new ThermalInformationBundle(pBundle,r.passiveTemp.get(i),r.thermalCoeff.get(i),r.decayChance));
                Overheated.LOGGER.info("CO: Adding Phase Transition for State: " + phaseState);
            }

        }
        addConservedEnergyBlock(UniversalState.of(AllSteamFluids.HEATED_HOT_AIR.gb.get()), UniversalState.empty,1000,1f,0.08f);
        addConservedEnergyBlock(UniversalState.of(AllSteamFluids.SUPERHEATED_HOT_AIR.gb.get()), UniversalState.empty,2000,4f,0.04f);
        addConservedEnergyBlock(UniversalState.of(AllSteamFluids.OVERHEATED_HOT_AIR.gb.get()), UniversalState.empty,10000,8f,0.002f);
        addConservedEnergyBlock(UniversalState.of(Blocks.IRON_BLOCK), UniversalState.empty,100000,16f,0.0008f);

    }

    public static void addConservedEnergyBlock(UniversalState block, UniversalState decayState, float passiveTemp, float thermalProperty, float decayChance){
        Overheated.LOGGER.info("Adding ConservedEnergyBlock of: [" + block + "]");
        PhaseTransitionMap.put(
                block,
                new ThermalInformationBundle(
                        new PhaseTransformBundle(decayState,true),
                        passiveTemp, thermalProperty, decayChance));
    }


    public static List<RecipeHolder<TemperatureTransformRecipe>> createRecipeCollection(){
        return level.getRecipeManager().getAllRecipesFor(AllRecipes.TEMPERATURE_TRANSFORM.TYPE.get());
    }
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        generateHandlers();
    }


}
