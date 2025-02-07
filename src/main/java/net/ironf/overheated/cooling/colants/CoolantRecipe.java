package net.ironf.overheated.cooling.colants;

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
import org.jetbrains.annotations.Nullable;

public class CoolantRecipe implements Recipe<SimpleContainer> {

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
    public static class Type implements RecipeType<CoolantRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "cooling";
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
    private final Integer heat;
    private final Float efficiency;
    private final Float minTemp;

    public Integer getHeat() {
        return heat;
    }

    public Float getEfficiency() {
        return efficiency;
    }
    public Float getMinTemp() {
        return minTemp;
    }


    public CoolantRecipe(ResourceLocation id, FluidIngredient input, Integer heat, Float efficiency, Float minTemp) {
        this.id = id;
        this.input = input;
        this.heat = heat;
        this.efficiency = efficiency;
        this.minTemp = minTemp;
    }

    public static class Serializer implements RecipeSerializer<CoolantRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Overheated.MODID, "cooling");

        @Override
        public CoolantRecipe fromJson(ResourceLocation id, JsonObject pSerializedRecipe) {
            FluidIngredient fluid = FluidIngredient.deserialize(GsonHelper.getAsJsonObject(pSerializedRecipe,"input_fluid"));
            Integer heat = GsonHelper.getAsInt(pSerializedRecipe,"heat");
            Float efficiency = GsonHelper.getAsFloat(pSerializedRecipe,"efficiency");
            Float minTemp = Math.abs(GsonHelper.getAsFloat(pSerializedRecipe,"min_temp"));


            return new CoolantRecipe(id,fluid,heat,efficiency,minTemp);

        }

        @Override
        public @Nullable CoolantRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidIngredient fluid = FluidIngredient.read(buf);
            Integer heat = buf.readInt();
            Float efficiency = buf.readFloat();
            Float minTemp = buf.readFloat();

            return new CoolantRecipe(id, fluid, heat,efficiency,minTemp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CoolantRecipe recipe) {
            recipe.input.write(buf);
            buf.writeInt(recipe.getHeat());
            buf.writeFloat(recipe.getEfficiency());
            buf.writeFloat(recipe.getMinTemp());
        }
    }
}
