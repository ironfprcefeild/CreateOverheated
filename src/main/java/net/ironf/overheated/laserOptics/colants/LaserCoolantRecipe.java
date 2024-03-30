package net.ironf.overheated.laserOptics.colants;

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

public class LaserCoolantRecipe implements Recipe<SimpleContainer> {

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
    public static class Type implements RecipeType<LaserCoolantRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "laser_cooling";
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
    private final Integer volatility;

    public Integer getHeat() {
        return heat;
    }

    public Integer getVolatility() {
        return volatility;
    }

    public LaserCoolantRecipe(ResourceLocation id, FluidIngredient input, Integer heat, Integer volatility) {
        this.id = id;
        this.input = input;
        this.heat = heat;
        this.volatility = volatility;
    }

    public static class Serializer implements RecipeSerializer<LaserCoolantRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Overheated.MODID, "laser_cooling");

        @Override
        public LaserCoolantRecipe fromJson(ResourceLocation id, JsonObject pSerializedRecipe) {
            FluidIngredient fluid = FluidIngredient.deserialize(GsonHelper.getAsJsonObject(pSerializedRecipe,"input_fluid"));
            Integer heat = GsonHelper.getAsInt(pSerializedRecipe,"heat");
            Integer volatility = GsonHelper.getAsInt(pSerializedRecipe,"volatility");
            return new LaserCoolantRecipe(id,fluid,heat,volatility);

        }

        @Override
        public @Nullable LaserCoolantRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidIngredient fluid = FluidIngredient.read(buf);
            Integer heat = buf.readInt();
            Integer volatility = buf.readInt();
            return new LaserCoolantRecipe(id, fluid, heat, volatility);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, LaserCoolantRecipe recipe) {
            recipe.input.write(buf);
            buf.writeInt(recipe.getHeat());
            buf.writeInt(recipe.getVolatility());
        }
    }
}
