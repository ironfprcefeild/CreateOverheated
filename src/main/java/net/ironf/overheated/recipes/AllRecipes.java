package net.ironf.overheated.recipes;

import com.mojang.serialization.MapCodec;
import mezz.jei.api.registration.IRecipeRegistration;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.colants.CoolantRecipe;
import net.ironf.overheated.metalWorking.TemperatureTransformRecipe;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserRecipe;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.function.Supplier;


public class AllRecipes {

    /// Registers
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Overheated.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Overheated.MODID);

    /// Entries
    public static final RecipeRegistration<SimpleFluidInput,CoolantRecipe> COOLANT = new RecipeRegistration<>(
            "cooling",
            CoolantRecipe.CoolantRecipeSerializer.CODEC,
            CoolantRecipe.CoolantRecipeSerializer.STREAM_CODEC,
            CoolantRecipe.class);
    public static final RecipeRegistration<DummyRecipeInput,PressureChamberRecipe> PRESSURE_CHAMBER = new RecipeRegistration<>(
            "pressure_chamber",
            PressureChamberRecipe.PressureChamberRecipeSerializer.CODEC,
            PressureChamberRecipe.PressureChamberRecipeSerializer.STREAM_CODEC,
            PressureChamberRecipe.class);
    public static final RecipeRegistration<DummyRecipeInput,CombustionRecipe> COMBUSTION = new RecipeRegistration<>(
            "combustion",
            CombustionRecipe.CombustionRecipeSerializer.CODEC,
            CombustionRecipe.CombustionRecipeSerializer.STREAM_CODEC,
            CombustionRecipe.class);
    public static final RecipeRegistration<SimpleItemInput,ImpactDrillRecipe> IMPACT_DRILLING = new RecipeRegistration<>(
            "impact_drilling",
            ImpactDrillRecipe.ImpactDrillSerializer.CODEC,
            ImpactDrillRecipe.ImpactDrillSerializer.STREAM_CODEC,
            ImpactDrillRecipe.class);
    public static final RecipeRegistration<SimpleFluidInput,CondenserRecipe> CONDENSING = new RecipeRegistration<>(
            "condensing",
            CondenserRecipe.CondenserSerializer.CODEC,
            CondenserRecipe.CondenserSerializer.STREAM_CODEC,
            CondenserRecipe.class);
    public static final RecipeRegistration<DummyRecipeInput, TemperatureTransformRecipe> TEMPERATURE_TRANSFORM = new RecipeRegistration<>(
            "phase_transition",
            TemperatureTransformRecipe.TemperatureTransformSerializer.CODEC,
            TemperatureTransformRecipe.TemperatureTransformSerializer.STREAM_CODEC,
            TemperatureTransformRecipe.class);




    /// Helpers
    public static class RecipeRegistration<I extends RecipeInput,T extends Recipe<I>> {
        public final Supplier<RecipeType<T>> TYPE;
        public final Supplier<RecipeSerializer<T>> SERIALIZER;
        public final MapCodec<T> MAP_CODEC;
        public final StreamCodec<RegistryFriendlyByteBuf,T> STREAM_CODEC;
        public final mezz.jei.api.recipe.RecipeType<T> JEIType;
        public RecipeRegistration(String id, MapCodec<T> mapCodec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Class<T> c){
            TYPE = TYPES.register(id, RecipeType::simple);

            SERIALIZER = SERIALIZERS.register(id, ()-> new RecipeSerializer<>() {
                @Override
                public MapCodec<T> codec() {
                    return MAP_CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                    return STREAM_CODEC;
                }
            });
            MAP_CODEC = mapCodec;
            STREAM_CODEC = streamCodec;

            JEIType = new mezz.jei.api.recipe.RecipeType<T>(Overheated.asResource(id),c);
        }

        public void JEIRegisterRecipes(RecipeManager rm, IRecipeRegistration JEIHandler){
            ArrayList<T> toAdd = new ArrayList<>();
            rm.getAllRecipesFor(this.TYPE.get()).forEach(rh -> toAdd.add(rh.value()));
            JEIHandler.addRecipes(JEIType,toAdd);
        }

    }





    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }


}
