package net.ironf.overheated.recipes;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.colants.CoolantRecipe;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserRecipe;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillRecipe;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.recipe.IndustrialBlastingRecipe;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.recipe.IndustrialMeltingRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;



public class AllRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Overheated.MODID);
    public static final RegistryObject<RecipeSerializer<CoolantRecipe>> COOLANT =
            SERIALIZERS.register("cooling", () -> CoolantRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<PressureChamberRecipe>> PRESSURE_CHAMBER =
            SERIALIZERS.register("pressure_chamber", () -> PressureChamberRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ImpactDrillRecipe>> IMPACT_DRILL =
            SERIALIZERS.register("impact_drilling", () -> ImpactDrillRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<CondenserRecipe>> CONDENSER =
            SERIALIZERS.register("condensing", () -> CondenserRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<IndustrialBlastingRecipe>> INDUSTRIAL_BLASTING =
            SERIALIZERS.register("industrial_blasting", () -> IndustrialBlastingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<IndustrialMeltingRecipe>> INDUSTRIAL_MELTING =
            SERIALIZERS.register("industrial_melting", () -> IndustrialMeltingRecipe.Serializer.INSTANCE);





    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }


}
