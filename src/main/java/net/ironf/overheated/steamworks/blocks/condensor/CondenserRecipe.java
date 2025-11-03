package net.ironf.overheated.steamworks.blocks.condensor;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.Overheated;
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
import net.minecraftforge.fluids.FluidStack;

public class CondenserRecipe implements Recipe<SimpleContainer> {


    @Override
    public boolean matches(SimpleContainer p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public FluidStack getOutput() {
        return output;
    }

    public Float getAddTemp() {
        return addTemp;
    }

    public Float getMinTemp() {
        return minTemp;
    }

    public static class Type implements RecipeType<CondenserRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "condensing";
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public FluidIngredient getInput() {
        return input;
    }

    private final ResourceLocation id;

    private final FluidIngredient input;

    private final FluidStack output;
    private final Float minTemp;
    private final Float addTemp;


    public CondenserRecipe(ResourceLocation id, FluidIngredient input, FluidStack output, Float minTemp, Float addTemp) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.minTemp = minTemp;
        this.addTemp = addTemp;
    }



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
            return new CondenserRecipe(id,fluid,output, minTemp, GsonHelper.getAsFloat(pSerializedRecipe,"addTemp"));
        }

        /*
            Read/Write Order
            1. Fluid Ingredeint
            2. Fluidstack Output
            3. minTemp
            4. addTemp
        */

        @Override
        public CondenserRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidIngredient fluid = FluidIngredient.read(buf);
            FluidStack output = FluidStack.readFromPacket(buf);
            float minTemp = buf.readFloat();
            float addTemp = buf.readFloat();
            return new CondenserRecipe(id, fluid, output, minTemp, addTemp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CondenserRecipe recipe) {
            recipe.input.write(buf);
            recipe.output.writeToPacket(buf);
            buf.writeFloat(recipe.minTemp);
            buf.writeFloat(recipe.addTemp);
        }
    }
}
