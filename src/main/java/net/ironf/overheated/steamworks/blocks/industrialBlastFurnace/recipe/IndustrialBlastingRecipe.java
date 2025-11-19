package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.BlastFurnaceStatus;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class IndustrialBlastingRecipe implements Recipe<SimpleContainer> {


    //// Vanilla Stuff so im not whined at
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
    private final int duration;

    public int getDuration() {return duration;}

    public BlastFurnaceStatus getRequirements() {
        return requirements;
    }

    public FluidStack getOutput() {
        return output;
    }

    public NonNullList<FluidIngredient> getFluidIngredients(){
        return ingredients;
    }

    public BlastFurnaceStatus getStatusRequirement(){return requirements;}

    public IndustrialBlastingRecipe(ResourceLocation id, FluidStack Output, NonNullList<FluidIngredient> inputs, BlastFurnaceStatus requirements, int duration){
        this.id = id;
        this.output = Output;
        this.ingredients = inputs;
        this.requirements = requirements;
        this.duration = duration;
    }

    public boolean testRecipe(BlastFurnaceControllerBlockEntity ibf, boolean simulate) {
        //Status match?
        if (!requirements.compareWith(ibf.BFData,null,true)){
            return false;
        }

        //Contain Input Fluids
        int totalDrain = 0;
        for (FluidIngredient fi : ingredients){
            int drained = ibf.MainTank.drainFluidIng(fi, IFluidHandler.FluidAction.SIMULATE);
            if (drained != fi.getRequiredAmount()){
                //We don't have enough of a fluid
                return false;
            }
            totalDrain += drained;
        }

        //Fluids Have Room?
        boolean outputIsGas = GasMapper.isGas(output) && output.getFluid().getFluidType().getDensity() < 0;
        if (!outputIsGas && ibf.MainTank.contained + output.getAmount() - totalDrain > ibf.MainTank.capacity){
            return false;
        }

        /// We are good to execute the recipe
        if (simulate){
            return true;
        }

        //This drains steam (or oxygen) and updates status
        requirements.compareWith(ibf,false);

        //Drain inputs
        for (FluidIngredient fi : ingredients){
            ibf.MainTank.drainFluidIng(fi, IFluidHandler.FluidAction.EXECUTE);
        }

        //Fill output
        if (outputIsGas){
            ibf.createGas(output);
        } else {
            ibf.MainTank.fill(output, IFluidHandler.FluidAction.EXECUTE);
        }

        return true;

    }




    public static class Serializer implements RecipeSerializer<IndustrialBlastingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("industrial_blasting");

        @Override
        public IndustrialBlastingRecipe fromJson(ResourceLocation id, JsonObject j) {
            JsonArray ingredients = GsonHelper.getAsJsonArray(j, "ingredients");
            NonNullList<FluidIngredient> inputs = NonNullList.withSize(ingredients.size(), FluidIngredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, FluidIngredient.deserialize(ingredients.get(i)));
            }

            return new IndustrialBlastingRecipe(id,
                    FluidIngredient.deserialize(GsonHelper.getAsJsonObject(j,"output")).getMatchingFluidStacks().get(0),
                    inputs,
                    new BlastFurnaceStatus(GsonHelper.getAsJsonObject(j,"status")),
                    GsonHelper.getAsInt(j,"duration"));
        }

        /* Network Order
            ingredient size
            ingredients
            output
            requirement
            duration
         */
        @Override
        public @Nullable IndustrialBlastingRecipe fromNetwork(ResourceLocation loc, FriendlyByteBuf buf) {
            NonNullList<FluidIngredient> inputs = NonNullList.withSize(buf.readInt(),FluidIngredient.EMPTY);
            inputs.replaceAll(ignored -> FluidIngredient.read(buf));
            return new IndustrialBlastingRecipe(loc,FluidStack.readFromPacket(buf),inputs,BlastFurnaceStatus.readFromBuffer(buf), buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, IndustrialBlastingRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (FluidIngredient ing : recipe.getFluidIngredients()) {
                ing.write(buf);
            }
            recipe.getOutput().writeToPacket(buf);
            recipe.getStatusRequirement().writeToBuffer(buf);
            buf.writeInt(recipe.duration);
        }
    }
}
