package net.ironf.overheated.utility.data.dataGeneration.recipes;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import net.ironf.overheated.AllFluids;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserRecipe;
import net.ironf.overheated.utility.data.dataGeneration.recipes.builders.OverheatedRecipeBuilder;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class OverheatedRecipeProvider extends RecipeProvider {
    public OverheatedRecipeProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> registries) {
        super(p,registries);
    }

    public static ArrayList<OverheatedRegistrate.MetallicSet> metallicSets = new ArrayList<>();
    public static ArrayList<OverheatedRegistrate.vanillaMetallicSet> vanillaMetallicSets = new ArrayList<>();
    public static ArrayList<OverheatedRegistrate.toolSet> toolSets = new ArrayList<>();

    @Override
    protected void buildRecipes(RecipeOutput writer) {
        ///Metallic Sets
        for (OverheatedRegistrate.MetallicSet Ms : metallicSets){
            Ms.buildRecipes(writer);
        }
        for (OverheatedRegistrate.vanillaMetallicSet Vms : vanillaMetallicSets){
            Vms.buildRecipes(writer);
        }

        /// Tool sets
        for (OverheatedRegistrate.toolSet Ts : toolSets){
            Ts.buildRecipes(writer);
        }

        /// Misc Metal Works

        //Copper nugget Melting
        //TODO fix copper nugget melting after reworking IBF
        /*
        writer.accept(getMeltingRecipe(
                Overheated.asResource("copper_nugget_melting"),
                AllItems.COPPER_NUGGET.asStack(),
                new FluidStack[]{new FluidStack(net.ironf.overheated.AllItems.COPPER_METALWORKS.molten.SOURCE.get().getSource(), mbPerIngot / 9)},
                OverheatedRegistrate.defaultMeltingRequirement.changeSteamAmount(mbPerIngot / 9 * 2),
                OverheatedRegistrate.meltTimePerIngot / 9));

         */


        /// Steam Condensing!
        //Loop over pressure levels
        AllSteamFluids.prepareSteamArray();
        for (int p = 0; p <= 4; p++){
            int h = 0;
            //loop over heat levels
            for (Fluid steam : AllSteamFluids.Steams[p]){
                new OverheatedRecipeBuilder<>(new CondenserRecipe(
                    FluidIngredient.of(new FluidStack(steam, 1)),
                    /*The pressure of the steam is 0 always if the heat is 0, making distilled water
                      If the steam is heated, it looses its heat and retains its pressure.
                      Only when the heat is 0, (when making distilled water), do we apply the p*5 term on the amount of distilled water output
                     */
                    AllSteamFluids.getSteamFromValues((h == 0) ? 0 : p, 0, (h == 0) ? p * 5 : 1),
                    0f,
                    (h == 0) ? (p * 3) : ((float) Math.floor(Math.pow(3, h + 1))),
                    new HeatData(h, 1)
                )).save(writer,
                    Overheated.asResource("steam_condensing/"+AllSteamFluids.heatingIDs[h]+"steam_"+AllSteamFluids.pressureIDs[p]));
                h++;
            }
        }

    }

    public static HashMap<Item,Item> conversionRecipes = new HashMap<>();
    public static void addMysteriousConversion(){
        for (Item ROI : conversionRecipes.keySet()){
            MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(
                    ROI.getDefaultInstance(),conversionRecipes.get(ROI).getDefaultInstance()));
        }
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(
                AllFluids.SLUDGE.BUCKET.get().getDefaultInstance(),AllFluids.STRAY_SAUCE.BUCKET.get().getDefaultInstance()));
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(
                AllSteamFluids.HEATED_STEAM_LOW.BUCKET.get().getDefaultInstance(), AllGasses.nitrogen.BUCKET.get().getDefaultInstance()));
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(
                AllSteamFluids.SUPERHEATED_STEAM_LOW.BUCKET.get().getDefaultInstance(), AllGasses.cinderfume.BUCKET.get().getDefaultInstance()));
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(
                AllSteamFluids.OVERHEATED_STEAM_LOW.BUCKET.get().getDefaultInstance(), AllGasses.voidaium.BUCKET.get().getDefaultInstance()));
    }
}
