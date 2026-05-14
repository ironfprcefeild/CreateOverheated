package net.ironf.overheated.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.colants.CoolantRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.checkerframework.checker.units.qual.C;

import java.util.function.Supplier;


public class AllRecipes {

    /// Registers
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Overheated.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Overheated.MODID);

    /// Entries
    public static final RecipeRegistration<CoolantRecipe> COOLANT = new RecipeRegistration<>(
            "coolant", CoolantRecipe.CoolantRecipeSerializer.CODEC, CoolantRecipe.CoolantRecipeSerializer.STREAM_CODEC);
    public static final RecipeRegistration<PressureChamberRecipe> PRESSURE_CHAMBER = new RecipeRegistration<>(
            "pressure_chamber", PressureChamberRecipe.PressureChamberRecipeSerializer.CODEC, PressureChamberRecipe.PressureChamberRecipeSerializer.STREAM_CODEC);
    public static final RecipeRegistration<CombustionRecipe> COMBUSTION = new RecipeRegistration<>(
            "combustion", CombustionRecipe.CombustionRecipeSerializer.CODEC, CombustionRecipe.CombustionRecipeSerializer.STREAM_CODEC);


    /// Helpers
    public static class RecipeRegistration<T extends Recipe<?>> {
        public final Supplier<RecipeType<T>> TYPE;
        public final Supplier<RecipeSerializer<T>> SERIALIZER;
        public final MapCodec<T> MAP_CODEC;
        public final StreamCodec<RegistryFriendlyByteBuf,T> STREAM_CODEC;
        public RecipeRegistration(String id, MapCodec<T> mapCodec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec){
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
        }
    }

    /*

    public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<ImpactDrillRecipe>> IMPACT_DRILL =
            SERIALIZERS.register("impact_drilling", () -> ImpactDrillRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<CondenserRecipe>> CONDENSER =
            SERIALIZERS.register("condensing", () -> CondenserRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<CombustionRecipe>> COMBUSTION =
            SERIALIZERS.register("combustion", () -> CombustionRecipe.Serializer.INSTANCE);


     */


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }


}
