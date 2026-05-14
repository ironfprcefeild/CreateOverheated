package net.ironf.overheated.steamworks.blocks.impactDrill;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.recipes.DummyRecipeInput;
import net.ironf.overheated.recipes.OverheatedCodecs;
import net.ironf.overheated.recipes.SimpleItemInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.mojang.serialization.Codec.FLOAT;
import static com.mojang.serialization.Codec.INT;

public class ImpactDrillRecipe implements Recipe<SimpleItemInput> {
    
    private final float torqueNeeded;
    private final float heatNeeded;
    private final float torqueImpact;
    private final int minPressure;
    private final FluidStack output;
    private final Ingredient input;
    private final float destructionChance;

    public float getTorqueNeeded() {
        return torqueNeeded;
    }
    public float getHeatNeeded() {
        return heatNeeded;
    }
    public FluidStack getOutput() {
        return output;
    }
    public Ingredient getInput() {
        return input;
    }
    public float getTorqueImpact() {
        return torqueImpact;
    }
    public float getDestructionChance() {return destructionChance;}
    public int getMinPressure() {
        return minPressure;
    }

    public ImpactDrillRecipe(float torqueNeeded, float torqueImpact, float heatNeeded, int minPressure, FluidStack output, Ingredient input, float destructionChance) {
        this.torqueNeeded = torqueNeeded;
        this.heatNeeded = heatNeeded;
        this.torqueImpact = torqueImpact;
        this.minPressure = minPressure;
        this.output = output;
        this.input = input;
        this.destructionChance = destructionChance;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipes.IMPACT_DRILLING.SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.IMPACT_DRILLING.TYPE.get();
    }
    
    /// Serializing
    public static class ImpactDrillSerializer implements RecipeSerializer<ImpactDrillRecipe> {
        public static final MapCodec<ImpactDrillRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FLOAT.fieldOf("minimum_torque").forGetter(ImpactDrillRecipe::getTorqueNeeded),
                FLOAT.fieldOf("torque_impact").forGetter(ImpactDrillRecipe::getTorqueImpact),
                FLOAT.fieldOf("heat").forGetter(ImpactDrillRecipe::getHeatNeeded),
                INT.fieldOf("minPressure").forGetter(ImpactDrillRecipe::getMinPressure),
                FluidStack.CODEC.fieldOf("output").forGetter(ImpactDrillRecipe::getOutput),
                Ingredient.CODEC.fieldOf("input").forGetter(ImpactDrillRecipe::getInput),
                FLOAT.fieldOf("destroy_chance").forGetter(ImpactDrillRecipe::getDestructionChance)
            ).apply(inst, ImpactDrillRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ImpactDrillRecipe> STREAM_CODEC =
                OverheatedCodecs.fatComposite(
                        ByteBufCodecs.FLOAT, ImpactDrillRecipe::getTorqueNeeded,
                        ByteBufCodecs.FLOAT, ImpactDrillRecipe::getTorqueImpact,
                        ByteBufCodecs.FLOAT, ImpactDrillRecipe::getHeatNeeded,
                        ByteBufCodecs.INT, ImpactDrillRecipe::getMinPressure,
                        FluidStack.STREAM_CODEC,ImpactDrillRecipe::getOutput,
                        Ingredient.CONTENTS_STREAM_CODEC, ImpactDrillRecipe::getInput,
                        ByteBufCodecs.FLOAT, ImpactDrillRecipe::getDestructionChance,
                        ImpactDrillRecipe::new
                );

        // Return our map codec.
        @Override
        public MapCodec<ImpactDrillRecipe> codec() {
            return CODEC;
        }

        // Return our stream codec.
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ImpactDrillRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
    
    /// Dummy Methods
    @Override
    public boolean matches(SimpleItemInput input, Level level) {
        return getInput().test(input.input());
    }

    @Override
    public ItemStack assemble(SimpleItemInput input, HolderLookup.Provider provider) {
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
    public static class Serializer implements RecipeSerializer<ImpactDrillRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource( "impact_drilling");

        @Override
        public ImpactDrillRecipe fromJson(ResourceLocation id, JsonObject j) {
            float torque = GsonHelper.getAsFloat(j,"minimum_torque");
            float torqueImpact = j.has("torque_impact") ? GsonHelper.getAsFloat(j,"torque_impact") : torque / 2;
            torqueImpact = torqueImpact > torque ? torque / 4 : torqueImpact;


            return new ImpactDrillRecipe(id,
                    torque,
                    j.has("heat") ? GsonHelper.getAsFloat(j,"heat") : 0,
                    torqueImpact,
                    j.has("pressure") ? GsonHelper.getAsInt(j,"pressure") : 0,
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j,"output")).getMatchingFluidStacks().get(0),
                    Ingredient.fromJson(GsonHelper.getAsJsonObject(j,"input")),
                    j.has("destroy_chance") ? GsonHelper.getAsFloat(j,"destroy_chance") : 0);
        }

        /*
        ////Read/Write Order
            1. torque
            2. heat
            3. torque impact
            4. min pressure
            5. output
            6. input
            7. Destruction Chance
         



        @Override
        public void toNetwork(FriendlyByteBuf buf, ImpactDrillRecipe recipe) {
            buf.writeFloat(recipe.getTorqueNeeded());
            buf.writeFloat(recipe.getHeatNeeded());
            buf.writeFloat(recipe.getTorqueImpact());
            buf.writeInt(recipe.getMinPressure());
            recipe.getOutput().writeToPacket(buf);
            recipe.getInput().toNetwork(buf);
            buf.writeFloat(recipe.getDestructionChance());
        }

        @Override
        public @Nullable ImpactDrillRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return  new ImpactDrillRecipe(id,
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readFluidStack(),
                    Ingredient.fromNetwork(buf),
                    buf.readFloat());
        }

    }
    
     */
}
