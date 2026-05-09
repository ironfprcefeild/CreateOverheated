package net.ironf.overheated.steamworks.blocks.pressureChamber.combustion;

import com.google.gson.JsonObject;
import net.ironf.overheated.Overheated;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public class CombustionRecipe implements Recipe<SimpleContainer> {


    /// Dummy Methods
    @Override
    public boolean matches(SimpleContainer p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }


    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }


    /// Actual Recipe Time
    private final ResourceLocation id;


    private final FluidIngredient inputFluidA;
    private final FluidIngredient inputFluidB;

    public FluidIngredient getInputFluidA() {
        return inputFluidA;
    }

    public FluidIngredient getInputFluidB() {
        return inputFluidB;
    }

    private final FluidStack outputFluid;

    public net.neoforged.neoforge.fluids.FluidStack getOutputFluid() {
        return outputFluid;
    }

    private final Integer combustionTime;

    public Integer getCombustionTime() {
        return combustionTime;
    }

    private final float laserHeat;
    private final int heatRating;

    public float getLaserHeat() {
        return laserHeat;
    }

    public int getHeatRating() {
        return heatRating;
    }

    public CombustionRecipe(ResourceLocation id, FluidIngredient inputFluidA, FluidIngredient inputFluidB, FluidStack outputFluid, Integer combustionTime, float laserHeat, int heatRating) {
        this.id = id;
        this.inputFluidA = inputFluidA;
        this.inputFluidB = inputFluidB;
        this.outputFluid = outputFluid;
        this.combustionTime = combustionTime;
        this.laserHeat = laserHeat;
        this.heatRating = heatRating;
    }


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

        /*
        Order:
            InputA
            InputB
            Output
            Time
         */
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
}
