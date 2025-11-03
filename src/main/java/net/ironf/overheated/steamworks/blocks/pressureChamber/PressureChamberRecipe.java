package net.ironf.overheated.steamworks.blocks.pressureChamber;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
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
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PressureChamberRecipe implements Recipe<SimpleContainer> {


    @Override
    public boolean matches(SimpleContainer container, Level level) {

        return false;
    }
    //IF simulate is true, returns true if recipe is valid for the chamber
    //IF simulate is false, returns true if recipe is valid for the chamber, but also will execute the recipe
    //IF set timer is true, and the recipe would return true, the chambers timer will be set
    public boolean testRecipe(ChamberCoreBlockEntity chamber, boolean fullSimulate, boolean setTimer){
        //Get input items
        IItemHandler availableItems = chamber.getInputItems();
        if (availableItems == null)
            return false;

        //Check if pressure is high enough and enough steam is in the chamber
        int chamberPressure = chamber.getPressure();
        if (!(chamberPressure >= SteamPressure && chamber.InputTank.getPrimaryHandler().getFluid().getAmount() >= ticksTaken)) {
            return false;
        }

        //Check if Heat is high enough
        if (!(chamber.getLaserHeat().getHeatOfLevel(minimumHeatRating) >= laserHeat))
            return false;

        //Make a list to store outputs eventually.
        List<ItemStack> recipeOutputItems = new ArrayList<>();

        //Simulate it first, and then do it again if all goes well
        for (boolean simulate : Iterate.trueAndFalse) {
            //If we are doing a full simulate, we should not actually extract anything, so return on the second lap.
            //Reaching this point on a full simulate mean that the recipe matches and we can return true
            if (!simulate && fullSimulate) {
                if (setTimer){
                    //Set timer, this code shouldn't be activated if the recipe doesn't match
                    chamber.setTimer(ticksTaken + 1);
                }
                return true;
            }

            //Get some information
            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            List<Ingredient> ingredients = new LinkedList<>(getIngredients());

            //Loop through each ingredient
            Ingredients:
            for (Ingredient ingredient : ingredients) {
                //Loop through every slot for each ingredient
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    //If the checked slot has less or the same items as the extracted items from that slot, continue and check the next slot.
                    //But only if simulating. If not simulating, see if the slot matches the ingredient.
                    if (simulate && availableItems.getStackInSlot(slot).getCount() <= extractedItemsFromSlot[slot])
                        continue;
                    ItemStack extracted = availableItems.extractItem(slot, 1, true);

                    //Item does not match, check next slot
                    if (!ingredient.test(extracted))
                        continue;
                    if (!simulate)
                        //TODO make this extract a count of items
                        //ACTUALLY FOR REAL extract the item because we are not simulating
                        availableItems.extractItem(slot, 1, false);
                    //Mark an extracted item
                    extractedItemsFromSlot[slot]++;
                    //Check next ingredient
                    continue Ingredients;
                }

                // something wasn't found, return false, recipe does not match
                return false;
            }

            //If we reached this point on the simulate lap, the recipe is all good without considering fitting the outputs. So we need to add the outputs to the list
            if (simulate) {
                recipeOutputItems.addAll(getOutputs());
            }

            //The accepts outputs method will add the items if simulate is false, meaning this will add the items on the second lap and complete the recipe
            if (!chamber.acceptOutputs(recipeOutputItems,simulate))
                return false;

            if (!simulate){
                //Add heat
                chamber.addHeat(heatAdded);
            }


        }
        if (setTimer){
            //Set timer, this code shouldn't be activated if the recipe doesn't match
            chamber.setTimer(ticksTaken + 1);
        }

        //Everything is good, recipe was executed if we're doing a full simulate, return true.
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

    //This method should not be used for this multi-output recipe

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    //This method should not be used for this multi-output recipe
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
        return PressureChamberRecipe.Serializer.INSTANCE;
    }
    public static class Type implements RecipeType<PressureChamberRecipe> {
        private Type() {}

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
    private final int minimumHeatRating;
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


    public PressureChamberRecipe(ResourceLocation id, int steamPressure, float laserHeat, float heatAdded, int ticksTaken, int minimumHeatRating,  NonNullList<Ingredient> inputs, NonNullList<ItemStack> outputs) {
        this.id = id;
        this.SteamPressure = steamPressure;
        this.laserHeat = laserHeat;
        this.heatAdded = heatAdded;
        this.ticksTaken = ticksTaken;
        this.minimumHeatRating = minimumHeatRating;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static class Serializer implements RecipeSerializer<PressureChamberRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                Overheated.asResource("pressure_chamber");

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


            int minHeatRate = SerializedRecipe.has("overheat") ? 3 : (SerializedRecipe.has("superheat") ? 2 : 0);

            return new PressureChamberRecipe(id,
                    GsonHelper.getAsInt(SerializedRecipe,"pressure"),
                    (SerializedRecipe.has("laser_heat") ? GsonHelper.getAsFloat(SerializedRecipe, "laser_heat") : 0),
                    GsonHelper.getAsFloat(SerializedRecipe,"heat_added"),
                    GsonHelper.getAsInt(SerializedRecipe,"ticks_taken"),
                    minHeatRate,
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
            9     Heat Rate
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
            return new PressureChamberRecipe(id,buf.readInt(),buf.readFloat(),buf.readFloat(),buf.readInt(), buf.readInt(),
                    inputs,outputs);
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
            buf.writeInt(recipe.minimumHeatRating);
        }
    }


}
