package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.ItemHelper;
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

public class IndustrialMeltingRecipe implements Recipe<SimpleContainer> {


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
        return IndustrialMeltingRecipe.Serializer.INSTANCE;
    }

    public boolean testRecipe(BlastFurnaceControllerBlockEntity ibf, boolean simulate) {
        //Status match?
        if (!requirements.compareWith(ibf.BFData,null,true)){
            return false;
        }

        //Items match?
        int matchingSlot = ItemHelper.findFirstMatchingSlotIndex(ibf.getInputItems(), ingredient);
        if(matchingSlot == -1){
            return false;
        }

        //Fluids have room?
        int totalFill = 0;
        for (FluidStack fs : getOutput()){
            totalFill += (GasMapper.isGas(fs)) ? 0 : (fs.getAmount());
        }
        if (ibf.MainTank.contained + totalFill > ibf.MainTank.capacity){
            return false;
        }

        /// Everything looks good, return if simulating.
        if (simulate){
            return true;
        }
        //This drains steam (or oxygen) and updates status
        requirements.compareWith(ibf,false);
        //This removes the input item
        ibf.getInputItems().extractItem(matchingSlot,1,false);
        //This adds the output fluid

        for (FluidStack fs : getOutput()){
            if (GasMapper.InvFluidGasMap.containsKey(fs.getFluid().getFluidType()) && fs.getFluid().getFluidType().getDensity() < 0){
                //Gasses
                ibf.createGas(fs);
            } else {
                ibf.MainTank.fill(fs, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        return true;
    }

    public static class Type implements RecipeType<IndustrialMeltingRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "industrial_melting";
    }

    @Override
    public RecipeType<?> getType() {return Type.INSTANCE;}

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final NonNullList<FluidStack> output;
    private final BlastFurnaceStatus requirements;
    private final int duration;
    public int getDuration(){return duration;}

    public BlastFurnaceStatus getStatusRequirement() {
        return requirements;
    }
    public NonNullList<FluidStack> getOutput() {
        return output;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public IndustrialMeltingRecipe(ResourceLocation id, NonNullList<FluidStack> Output, Ingredient inputs, BlastFurnaceStatus requirements, int duration){
        this.id = id;
        this.output = Output;
        this.ingredient = inputs;
        this.requirements = requirements;
        this.duration = duration;
    }

    public static class Serializer implements RecipeSerializer<IndustrialMeltingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("industrial_melting");

        @Override
        public IndustrialMeltingRecipe fromJson(ResourceLocation id, JsonObject j) {

            JsonArray rawOutputs = GsonHelper.getAsJsonArray(j, "ingredients");
            NonNullList<FluidStack> outputs = NonNullList.withSize(rawOutputs.size(), FluidStack.EMPTY);
            rawOutputs.forEach(f -> outputs.add(FluidIngredient.deserialize(f).matchingFluidStacks.get(0)));

            return new IndustrialMeltingRecipe(id,
                    outputs,
                    Ingredient.fromJson(GsonHelper.getAsJsonObject(j,"input")),
                    new BlastFurnaceStatus(GsonHelper.getAsJsonObject(j,"status")),
                    GsonHelper.getAsInt(j,"duration")
            );
        }

        /* Network Order
            outputs size
            outputs
            input
            requirement
            duration
         */
        @Override
        public @Nullable IndustrialMeltingRecipe fromNetwork(ResourceLocation loc, FriendlyByteBuf buf) {
            NonNullList<FluidStack> outputs = NonNullList.withSize(buf.readInt(),FluidStack.EMPTY);
            outputs.replaceAll(ignored -> FluidStack.readFromPacket(buf));
            return new IndustrialMeltingRecipe(loc,outputs,Ingredient.fromNetwork(buf),BlastFurnaceStatus.readFromBuffer(buf),buf.readInt() );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, IndustrialMeltingRecipe recipe) {
            buf.writeInt(recipe.getOutput().size());
            for (FluidStack fs : recipe.getOutput()) {
                fs.writeToPacket(buf);
            }
            recipe.getIngredient().toNetwork(buf);
            recipe.getStatusRequirement().writeToBuffer(buf);
            buf.writeInt(recipe.getDuration());
        }
    }


}
