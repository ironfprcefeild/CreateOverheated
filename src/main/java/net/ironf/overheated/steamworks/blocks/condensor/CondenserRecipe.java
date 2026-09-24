package net.ironf.overheated.steamworks.blocks.condensor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.recipes.OverheatedCodecs;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import static com.mojang.serialization.Codec.FLOAT;

public class CondenserRecipe implements Recipe<SimpleFluidInput> {

    private final FluidIngredient input;
    private final FluidStack output;
    private final Float minTemp;
    private final Float addTemp;
    private final HeatData generatedHeat;

    public FluidStack getOutput() {
        return output;
    }
    public Float getAddTemp() {
        return addTemp;
    }
    public Float getMinTemp() {
        return minTemp;
    }
    public HeatData getGeneratedHeat() {
        return generatedHeat;
    }
    public FluidIngredient getInput() {
        return input;
    }

    public CondenserRecipe(FluidIngredient input, FluidStack output, Float minTemp, Float addTemp, HeatData generatedHeat) {
        this.input = input;
        this.output = output;
        this.minTemp = minTemp;
        this.addTemp = addTemp;
        this.generatedHeat = generatedHeat;
    }

    public CondenserRecipe(FluidIngredient input, FluidStack output, Float minTemp, Float addTemp, Float heat, Float superHeat, Float overheat) {
        this.input = input;
        this.output = output;
        this.minTemp = minTemp;
        this.addTemp = addTemp;
        this.generatedHeat = new HeatData(heat,superHeat,overheat);
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipes.CONDENSING.SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.CONDENSING.TYPE.get();
    }

    /// Serializing
    public static class CondenserSerializer implements RecipeSerializer<CondenserRecipe> {
        public static final MapCodec<CondenserRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FluidIngredient.CODEC.fieldOf("input").forGetter(CondenserRecipe::getInput),
            FluidStack.CODEC.fieldOf("output").forGetter(CondenserRecipe::getOutput),
            FLOAT.fieldOf("minTemp").forGetter(CondenserRecipe::getMinTemp),
            FLOAT.fieldOf("addTemp").forGetter(CondenserRecipe::getAddTemp),
            FLOAT.fieldOf("heat").forGetter((O) -> O.getGeneratedHeat().Heat),
            FLOAT.fieldOf("superHeat").forGetter((O) -> O.getGeneratedHeat().SuperHeat),
            FLOAT.fieldOf("overHeat").forGetter((O) -> O.getGeneratedHeat().OverHeat)
        ).apply(inst, CondenserRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        FluidIngredient.STREAM_CODEC, CondenserRecipe::getInput,
                        FluidStack.STREAM_CODEC, CondenserRecipe::getOutput,
                        ByteBufCodecs.FLOAT, CondenserRecipe::getMinTemp,
                        ByteBufCodecs.FLOAT, CondenserRecipe::getAddTemp,
                        OverheatedCodecs.HEAT_DATA, CondenserRecipe::getGeneratedHeat,
                        CondenserRecipe::new
                );

        // Return our map codec.
        @Override
        public MapCodec<CondenserRecipe> codec() {
            return CODEC;
        }

        // Return our stream codec.
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    /// Dummy Methods
    @Override
    public boolean matches(SimpleFluidInput simpleFluidInput, Level level) {
        return input.test(simpleFluidInput.fluid());
    }

    @Override
    public ItemStack assemble(SimpleFluidInput simpleFluidInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }






/*
    public static class Serializer implements RecipeSerializer<CondenserRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("condensing");

        @Override
        public CondenserRecipe fromJson(ResourceLocation id, JsonObject pSerializedRecipe) {
            FluidIngredient fluid = FluidIngredient.deserialize(GsonHelper.getAsJsonObject(pSerializedRecipe,"input"));
            FluidStack output = FluidIngredient.deserialize(GsonHelper.getAsJsonObject(pSerializedRecipe,"output")).getMatchingFluidStacks().get(0);
            float minTemp = 0;
            if (pSerializedRecipe.has("minQuality")){
                minTemp = -GsonHelper.getAsFloat(pSerializedRecipe,"minQuality");
            } else if (pSerializedRecipe.has("minTemp")){
                minTemp = GsonHelper.getAsFloat(pSerializedRecipe,"minTemp");
            }
            HeatData heat = HeatData.empty();
            if (pSerializedRecipe.has("outputHeat")) {
                int heatLevel = pSerializedRecipe.has("outputHeatLevel")
                        ? GsonHelper.getAsInt(pSerializedRecipe, "outputHeatLevel")
                        : pSerializedRecipe.has("overheat") ? 3 : (pSerializedRecipe.has("superheat") ? 2 : 0);
                heat = new HeatData(heatLevel, GsonHelper.getAsInt(pSerializedRecipe, "outputHeat"));
            }
            return new CondenserRecipe(id,fluid,output, minTemp, GsonHelper.getAsFloat(pSerializedRecipe,"addTemp"), heat);
        }


        @Override
        public CondenserRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidIngredient fluid = FluidIngredient.read(buf);
            FluidStack output = FluidStack.readFromPacket(buf);
            float minTemp = buf.readFloat();
            float addTemp = buf.readFloat();
            int heatlevel = buf.readInt();
            int heatAmount = buf.readInt();
            return new CondenserRecipe(id, fluid, output, minTemp, addTemp, new HeatData(heatlevel,heatAmount));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CondenserRecipe recipe) {
            recipe.input.write(buf);
            recipe.output.writeToPacket(buf);
            buf.writeFloat(recipe.minTemp);
            buf.writeFloat(recipe.addTemp);
            int heatLevel = recipe.generatedHeat.getHeatLevelOfHighest();
            buf.writeInt(heatLevel);
            buf.writeInt((int) recipe.generatedHeat.getHeatOfLevel(heatLevel));
        }
    }

        /*
    public static class Type implements RecipeType<CondenserRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "condensing";
    }

     */

}
