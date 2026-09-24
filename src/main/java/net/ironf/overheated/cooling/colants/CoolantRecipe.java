package net.ironf.overheated.cooling.colants;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.recipes.SimpleFluidInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.NotNull;

import static com.mojang.serialization.Codec.FLOAT;
import static com.mojang.serialization.Codec.INT;

public class CoolantRecipe implements Recipe<SimpleFluidInput> {


    private final FluidIngredient input;
    private final Integer heat;
    private final Float efficiency;
    private final Float minTemp;

    public CoolantRecipe(FluidIngredient input, Integer heat, Float efficiency, Float minTemp) {
        this.input = input;
        this.heat = heat;
        this.efficiency = efficiency;
        this.minTemp = minTemp;
    }

    @Override
    public boolean matches(SimpleFluidInput coolantRecipeInput, Level level) {
        return input.test(coolantRecipeInput.fluid());
    }

    public FluidIngredient getInputFluid(){
        return input;
    }
    public Float getEfficiency() {
        return efficiency;
    }
    public Float getMinTemp() {
        return minTemp;
    }
    public Integer getHeat() {
        return heat;
    }



    //Serializing
    public static class CoolantRecipeSerializer implements RecipeSerializer<CoolantRecipe> {
        public static final MapCodec<CoolantRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(CoolantRecipe::getInputFluid),
                INT.fieldOf("heat").forGetter(CoolantRecipe::getHeat),
                FLOAT.fieldOf("efficiency").forGetter(CoolantRecipe::getEfficiency),
                FLOAT.fieldOf("min_temp").forGetter(CoolantRecipe::getMinTemp)
        ).apply(inst, CoolantRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CoolantRecipe> STREAM_CODEC =
                StreamCodec.composite(
                    FluidIngredient.STREAM_CODEC,CoolantRecipe::getInputFluid,
                    ByteBufCodecs.INT, CoolantRecipe::getHeat,
                    ByteBufCodecs.FLOAT, CoolantRecipe::getEfficiency,
                    ByteBufCodecs.FLOAT, CoolantRecipe::getMinTemp,
                    CoolantRecipe::new
                );

        // Return our map codec.
        @Override
        public MapCodec<CoolantRecipe> codec() {
            return CODEC;
        }

        // Return our stream codec.
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CoolantRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    //Dummy Methods
    @Override
    public @NotNull ItemStack assemble(SimpleFluidInput coolantRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return AllRecipes.COOLANT.SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<SimpleFluidInput>> getType() {
        return AllRecipes.COOLANT.TYPE.get();
    }

}
