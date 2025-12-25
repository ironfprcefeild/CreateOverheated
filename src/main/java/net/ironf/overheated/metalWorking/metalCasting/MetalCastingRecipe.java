package net.ironf.overheated.metalWorking.metalCasting;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.Overheated;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MetalCastingRecipe implements Recipe<SimpleContainer> {


    ////Vanilla Stuff so im not whined at
    //Theese methods should not be used.
    @Override public boolean matches(SimpleContainer p_44002_, Level p_44003_) {return false;}
    @Override public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {return ItemStack.EMPTY;}
    @Override public NonNullList<Ingredient> getIngredients() {return null;}
    @Override public boolean canCraftInDimensions(int p_43999_, int p_44000_) {return true;}
    @Override public ItemStack getResultItem(RegistryAccess p_267052_) {return solidCasted;}

    //// Actual Recipe Logic

    @Override
    public ResourceLocation getId() {
        return id;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return MetalCastingRecipe.Serializer.INSTANCE;
    }

    public static class Type implements RecipeType<MetalCastingRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "metal_casting";
    }

    @Override
    public RecipeType<?> getType() {return Type.INSTANCE;}

    private final ResourceLocation id;
    private final FluidIngredient input;
    private final ItemStack solidCasted;
    private final int duration;
    public int getDuration(){return duration;}

    public MetalCastingRecipe(ResourceLocation id, FluidIngredient input, ItemStack solidCasted, int duration){
        this.id = id;
        this.input = input;
        this.solidCasted = solidCasted;

        this.duration = duration;
    }

    public static class Serializer implements RecipeSerializer<MetalCastingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("metal_casting");

        @Override
        public MetalCastingRecipe fromJson(ResourceLocation id, JsonObject j) {
            return new MetalCastingRecipe(
                    id,
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j,"input")),
                    Ingredient.fromJson(GsonHelper.getAsJsonObject(j,"solid_cast")).getItems()[0],
                    GsonHelper.getAsInt(j,"duration"));
        }

        @Override
        public @Nullable MetalCastingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new MetalCastingRecipe(
                    id,
                    FluidIngredient.read(buf),
                    buf.readItem(),
                    buf.readInt());

        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MetalCastingRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItem(recipe.solidCasted);
            buf.writeInt(recipe.duration);
        }
    }

    public BlockState getStateToPlace(){
        if (solidCasted.getItem() instanceof BlockItem BI){
            return BI.getBlock().defaultBlockState();
        } else {
            return null;
        }
    }


}
