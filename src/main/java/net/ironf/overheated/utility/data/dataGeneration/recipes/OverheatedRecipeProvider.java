package net.ironf.overheated.utility.data.dataGeneration.recipes;

import com.simibubi.create.AllItems;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.function.Consumer;

import static net.ironf.overheated.utility.registration.OverheatedRegistrate.mbPerIngot;
import static net.ironf.overheated.utility.registration.RecipeBuilders.getMeltingRecipe;

public class OverheatedRecipeProvider extends RecipeProvider {
    public OverheatedRecipeProvider(PackOutput p) {
        super(p);
    }



    public static ArrayList<OverheatedRegistrate.MetallicSet> metallicSets = new ArrayList<>();
    public static ArrayList<OverheatedRegistrate.vanillaMetallicSet> vanillaMetallicSets = new ArrayList<>();
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        ///Metallic Sets
        for (OverheatedRegistrate.MetallicSet Ms : metallicSets){
            Ms.buildRecipes(writer);
        }
        for (OverheatedRegistrate.vanillaMetallicSet Vms : vanillaMetallicSets){
            Vms.buildRecipes(writer);
        }
        //Copper nugget Melting
        writer.accept(getMeltingRecipe(
                Overheated.asResource("copper_nugget_melting"),
                AllItems.COPPER_NUGGET.asStack(),
                new FluidStack[]{new FluidStack(net.ironf.overheated.AllItems.COPPER_METALWORKS.molten.SOURCE.get().getSource(), mbPerIngot / 9)},
                OverheatedRegistrate.defaultMeltingRequirement.changeSteamAmount(mbPerIngot / 9 * 2),
                OverheatedRegistrate.meltTimePerIngot / 9));


    }
}
