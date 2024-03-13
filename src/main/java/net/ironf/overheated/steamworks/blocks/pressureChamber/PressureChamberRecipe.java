package net.ironf.overheated.steamworks.blocks.pressureChamber;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.ironf.overheated.Overheated;
import net.minecraft.core.NonNullList;
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
import org.jetbrains.annotations.Nullable;

public class PressureChamberRecipe implements Recipe<SimpleContainer> {

    //limitations with this method:
    //If 1 item matches multiple tags in a recipe this can cause a lot of issues because the will both find the same item in the vault to sue
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        Overheated.LOGGER.info("Matching a chamber recipe");

        //see if we have a good option for every ingredient
        for (Ingredient item : inputs){
            for (int slot = 0; slot < container.getContainerSize(); slot++){
                if (item.test(container.getItem(slot))){
                    //We found a match for this ingredient, so we can move on
                    break;
                }
                //Continue our search for a match
            }
            //We found no match for this ingredient, return false
            return false;
        }
        //We found matches for all ingredients, return true
        return true;

    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

    //This method should not be used for this multi-output recipe
    @Override
    public ItemStack assemble(SimpleContainer p_44001_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    //This method should not be used for this multi-output recipe
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PressureChamberRecipe.Serializer.INSTANCE;
    }
    public static class Type implements RecipeType<PressureChamberRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "pressure_chamber";
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    private final ResourceLocation id;
    private final int SteamPressure;
    private final float laserHeat;
    private final float heatAdded;
    private final int ticksTaken;
    private final NonNullList<Ingredient> inputs;
    private final NonNullList<ItemStack> outputs;

    public float getLaserHeat() {
        return laserHeat;
    }

    public int getSteamPressure() {
        return SteamPressure;
    }
    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }

    public int getTicksTaken() {return ticksTaken;}

    public float getHeatAdded() {
        return heatAdded;
    }

    public PressureChamberRecipe(ResourceLocation id, int steamPressure, float laserHeat, float heatAdded, int ticksTaken, NonNullList<Ingredient> inputs, NonNullList<ItemStack> outputs) {
        this.id = id;
        this.SteamPressure = steamPressure;
        this.laserHeat = laserHeat;
        this.heatAdded = heatAdded;
        this.ticksTaken = ticksTaken;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static class Serializer implements RecipeSerializer<PressureChamberRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Overheated.MODID, "pressure_chamber");

        @Override
        public PressureChamberRecipe fromJson(ResourceLocation id, JsonObject SerializedRecipe) {

            JsonArray ingredients = GsonHelper.getAsJsonArray(SerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            JsonArray itemStacks = GsonHelper.getAsJsonArray(SerializedRecipe,"outputs");
            NonNullList<ItemStack> outputs = NonNullList.withSize(itemStacks.size(),ItemStack.EMPTY);
            for (int i = 0; i < outputs.size(); i++){
                outputs.set(i,Ingredient.fromJson(itemStacks.get(i)).getItems()[0]);
            }

            return new PressureChamberRecipe(id,
                    GsonHelper.getAsInt(SerializedRecipe,"pressure"),
                    (SerializedRecipe.has("laser_heat") ? GsonHelper.getAsFloat(SerializedRecipe, "laser_heat") : 0),
                    GsonHelper.getAsFloat(SerializedRecipe,"heat_added"),
                    GsonHelper.getAsInt(SerializedRecipe,"ticks_taken"),
                    inputs,
                    outputs);

        }

        /*
            Read/Write Ordering:
            1     Input Size
            2...  Inputs
            3     Output Size
            4...  Outputs
            5     Pressure
            6     Laser Heat
            7     Added Heat
            8     Ticks Taken
         */
        @Override
        public @Nullable PressureChamberRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }
            NonNullList<ItemStack> outputs = NonNullList.withSize(buf.readInt(),ItemStack.EMPTY);
            for (int i = 0; i < outputs.size(); i++) {
                outputs.set(i, buf.readItem());
            }
            return new PressureChamberRecipe(id,buf.readInt(),buf.readFloat(),buf.readFloat(),buf.readInt(),inputs,outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PressureChamberRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            for (ItemStack itm : recipe.getOutputs()) {
                buf.writeItem(itm);
            }
            buf.writeInt(recipe.SteamPressure);
            buf.writeFloat(recipe.laserHeat);
            buf.writeFloat(recipe.heatAdded);
            buf.writeInt(recipe.ticksTaken);
        }
    }
}
