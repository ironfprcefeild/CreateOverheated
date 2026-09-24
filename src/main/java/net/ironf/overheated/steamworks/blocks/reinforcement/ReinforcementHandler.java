package net.ironf.overheated.steamworks.blocks.reinforcement;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;

public class ReinforcementHandler {

    public static ArrayList<ItemStack> itemsWithReinforcement = new ArrayList<>();


    public static ArrayList<String> requiresReinforcement = new ArrayList<>();
    static {
        requiresReinforcement.add("misc/nihilite/compacting_liquid_nihilite");
        requiresReinforcement.add("misc/thermolysis/copper_chloride");
        requiresReinforcement.add("misc/thermolysis/cupric_oxide");
        requiresReinforcement.add("misc/thermolysis/overheated");
        requiresReinforcement.add("misc/thermolysis/superheated");
        requiresReinforcement.add("misc/thermolysis/oxygen");
        requiresReinforcement.add("misc/thermolysis/hydrogen");
        requiresReinforcement.add("misc/ammonia");
        requiresReinforcement.add("misc/ammonia_bonemeal");
        requiresReinforcement.add("misc/cinderfume/cinder_globule");
        requiresReinforcement.add("misc/cinderfume/rod_from_cinder_globule");
        requiresReinforcement.add("misc/hydrogen_oxygen_combustion");

    }

    public static boolean reinforcedRecipe(Recipe<?> r){
        if (r instanceof BasinRecipe basinRecipe){
            if (!basinRecipe.getRollableResults().isEmpty()){
                if (!itemsWithReinforcement.containsAll(basinRecipe.getRollableResultsAsItemStacks())) {
                    return false;
                }
            }
        }
        return false;
    }






}
