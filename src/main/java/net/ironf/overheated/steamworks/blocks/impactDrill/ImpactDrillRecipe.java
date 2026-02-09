package net.ironf.overheated.steamworks.blocks.impactDrill;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.Overheated;
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
import org.jetbrains.annotations.Nullable;

public class ImpactDrillRecipe implements Recipe<SimpleContainer> {




    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return input.test(pContainer.getItem(0));
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
        return Id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }
    public static class Type implements RecipeType<ImpactDrillRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "impact_drilling";
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    private final ResourceLocation Id;
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

    public ImpactDrillRecipe(ResourceLocation id, float torqueNeeded, float heatNeeded, float torqueImpact, int minPressure, FluidStack output, Ingredient input, float destructionChance) {
        Id = id;
        this.torqueNeeded = torqueNeeded;
        this.heatNeeded = heatNeeded;
        this.torqueImpact = torqueImpact;
        this.minPressure = minPressure;
        this.output = output;
        this.input = input;
        this.destructionChance = destructionChance;
    }
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
         */



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
}
