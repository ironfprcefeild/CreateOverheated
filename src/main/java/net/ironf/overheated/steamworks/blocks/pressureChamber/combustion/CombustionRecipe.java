package net.ironf.overheated.steamworks.blocks.pressureChamber.combustion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.recipes.DummyRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import static com.mojang.serialization.Codec.FLOAT;
import static com.mojang.serialization.Codec.INT;

public class CombustionRecipe implements Recipe<DummyRecipeInput> {

    /// Actual Recipe Time
    private final FluidIngredient inputFluidA;
    private final FluidIngredient inputFluidB;
    private final FluidStack outputFluid;
    private final Integer combustionTime;
    private final float laserHeat;
    private final int heatRating;

    public float getLaserHeat() {
        return laserHeat;
    }
    public int getHeatRating() {
        return heatRating;
    }
    public Integer getCombustionTime() {
        return combustionTime;
    }
    public FluidStack getOutputFluid() {
        return outputFluid;
    }
    public FluidIngredient getInputFluidA() {
        return inputFluidA;
    }
    public FluidIngredient getInputFluidB() {
        return inputFluidB;
    }

    public CombustionRecipe(FluidIngredient inputFluidA, FluidIngredient inputFluidB, FluidStack outputFluid, Integer combustionTime, float laserHeat, int heatRating) {
        this.inputFluidA = inputFluidA;
        this.inputFluidB = inputFluidB;
        this.outputFluid = outputFluid;
        this.combustionTime = combustionTime;
        this.laserHeat = laserHeat;
        this.heatRating = heatRating;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipes.COMBUSTION.SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.COMBUSTION.TYPE.get();
    }
    
    /// Serializing
    public static class CombustionRecipeSerializer implements RecipeSerializer<CombustionRecipe> {
        public static final MapCodec<CombustionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidIngredient.CODEC.fieldOf("input_a").forGetter(CombustionRecipe::getInputFluidA),
                FluidIngredient.CODEC.fieldOf("input_b").forGetter(CombustionRecipe::getInputFluidB),
                FluidStack.CODEC.fieldOf("output").forGetter(CombustionRecipe::getOutputFluid),
                INT.fieldOf("time").forGetter(CombustionRecipe::getCombustionTime),
                FLOAT.fieldOf("laser_heat").forGetter(CombustionRecipe::getLaserHeat),
                INT.fieldOf("heat_rating").forGetter(CombustionRecipe::getHeatRating)
        ).apply(inst, CombustionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CombustionRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        FluidIngredient.STREAM_CODEC,CombustionRecipe::getInputFluidA,
                        FluidIngredient.STREAM_CODEC,CombustionRecipe::getInputFluidB,
                        FluidStack.STREAM_CODEC,CombustionRecipe::getOutputFluid,
                        ByteBufCodecs.INT, CombustionRecipe::getCombustionTime,
                        ByteBufCodecs.FLOAT, CombustionRecipe::getLaserHeat,
                        ByteBufCodecs.INT, CombustionRecipe::getHeatRating,
                        CombustionRecipe::new
                );

        // Return our map codec.
        @Override
        public MapCodec<CombustionRecipe> codec() {
            return CODEC;
        }

        // Return our stream codec.
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CombustionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
    


    /*
    public static class Type implements RecipeType<CombustionRecipe> {
        private Type() {
        }

        public static final CombustionRecipe.Type INSTANCE = new Type();
        public static final String ID = "combustion";
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<CombustionRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("combustion");


        @Override
        public CombustionRecipe fromJson(ResourceLocation id, JsonObject j) {
            int minHeatRate = j.has(   "overheat") ? 3 : (j.has("superheat") ? 2 : 0);
            float laserHeat =  (j.has("laser_heat") ? GsonHelper.getAsFloat(j, "laser_heat") : 0);
            return new CombustionRecipe(id,
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j, "input_a")),
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j, "input_b")),
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j, "output")).getMatchingFluidStacks().get(0),
                    GsonHelper.getAsInt(j, "time"),laserHeat,minHeatRate);
        }

        @Override
        public @Nullable CombustionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new CombustionRecipe(
                    id,
                    FluidIngredient.read(buf),
                    FluidIngredient.read(buf),
                    FluidStack.readFromPacket(buf),
                    buf.readInt(),
                    buf.readFloat(),
                    buf.readInt()
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CombustionRecipe recipe) {
            recipe.inputFluidA.write(buf);
            recipe.inputFluidB.write(buf);
            recipe.outputFluid.writeToPacket(buf);
            buf.writeInt(recipe.combustionTime);
            buf.writeFloat(recipe.laserHeat);
            buf.writeFloat(recipe.heatRating);
        }
    }
    
     */
    /// Dummy Methods
    @Override
    public boolean matches(DummyRecipeInput dummyRecipeInput, Level level) {
        return false;
    }
    @Override
    public ItemStack assemble(DummyRecipeInput dummyRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canCraftInDimensions(int p1, int p2) {
        return true;
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }



}
