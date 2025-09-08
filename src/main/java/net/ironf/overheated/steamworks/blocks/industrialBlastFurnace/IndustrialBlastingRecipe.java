package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IndustrialBlastingRecipe implements Recipe<SimpleContainer> {


    ////Vanilla Stuff so im not whined at
    //Theese methods should not be used.
    @Override public boolean matches(SimpleContainer p_44002_, Level p_44003_) {return false;}
    @Override public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {return ItemStack.EMPTY;}
    @Override public NonNullList<Ingredient> getIngredients() {return null;}
    @Override public boolean canCraftInDimensions(int p_43999_, int p_44000_) {return true;}
    @Override public ItemStack getResultItem(RegistryAccess p_267052_) {return ItemStack.EMPTY;}

    //// Actual Recipe Logic

    @Override
    public ResourceLocation getId() {
        return id;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return IndustrialBlastingRecipe.Serializer.INSTANCE;
    }
    public static class Type implements RecipeType<IndustrialBlastingRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "industrial_blasting";
    }

    @Override
    public RecipeType<?> getType() {return Type.INSTANCE;}

    private final ResourceLocation id;
    private final NonNullList<FluidIngredient> ingredients;
    private final FluidStack output;
    private final BlastFurnaceStatus requirements;

    public BlastFurnaceStatus getRequirements() {
        return requirements;
    }

    public FluidStack getOutput() {
        return output;
    }

    public NonNullList<FluidIngredient> getFluidIngredients(){
        return ingredients;
    }

    public IndustrialBlastingRecipe(ResourceLocation id, FluidStack Output, NonNullList<FluidIngredient> inputs, BlastFurnaceStatus requirements){
        this.id = id;
        this.output = Output;
        this.ingredients = inputs;
        this.requirements = requirements;
    }

    public static class Serializer implements RecipeSerializer<IndustrialBlastingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("industrial_blasting");

        @Override
        public IndustrialBlastingRecipe fromJson(ResourceLocation id, JsonObject j) {
            JsonArray ingredients = GsonHelper.getAsJsonArray(j, "ingredients");
            NonNullList<FluidIngredient> inputs = NonNullList.withSize(ingredients.size(), FluidIngredient.EMPTY);
            return new IndustrialBlastingRecipe(id,
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j,"output")).getMatchingFluidStacks().get(0),
                    inputs,
                    new BlastFurnaceStatus(GsonHelper.getAsJsonObject(j,"status"))
            );
        }

        @Override
        public @Nullable IndustrialBlastingRecipe fromNetwork(ResourceLocation p_44105_, FriendlyByteBuf p_44106_) {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf p_44101_, IndustrialBlastingRecipe p_44102_) {

        }
    }
}
