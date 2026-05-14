package net.ironf.overheated.steamworks.blocks.pressureChamber;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.cooling.colants.CoolantRecipe;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.recipes.DummyRecipeInput;
import net.ironf.overheated.recipes.OverheatedCodecs;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.mojang.serialization.Codec.*;

public class PressureChamberRecipe implements Recipe<DummyRecipeInput> {
    private final int SteamPressure;
    private final float laserHeat;
    private final float heatAdded;
    private final int ticksTaken;
    private final int minimumHeatRating;
    private final boolean combustion;
    private final NonNullList<Ingredient> inputs;
    private final NonNullList<ItemStack> outputs;

    public Float getLaserHeat() {
        return laserHeat;
    }
    public Integer getSteamPressure() {
        return SteamPressure;
    }
    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }
    public NonNullList<Ingredient> getInputs() {
        return inputs;
    }
    public Integer getTicksTaken() {return ticksTaken;}
    public Float getHeatAdded() {
        return heatAdded;
    }
    public Boolean isCombustion() {
        return combustion;
    }
    public Integer getMinimumHeatRating() {return minimumHeatRating;}

    public PressureChamberRecipe(int steamPressure, float laserHeat, float heatAdded, int ticksTaken, int minimumHeatRating, List<Ingredient> inputs, List<ItemStack> outputs, boolean combustion) {
        this.SteamPressure = steamPressure;
        this.laserHeat = laserHeat;
        this.heatAdded = heatAdded;
        this.ticksTaken = ticksTaken;
        this.minimumHeatRating = minimumHeatRating;
        this.inputs = NonNullList.copyOf(inputs);
        this.outputs = NonNullList.copyOf(outputs);
        this.combustion = combustion;
    }

    //IF simulate is true, returns true if recipe is valid for the chamber
    //IF simulate is false, returns true if recipe is valid for the chamber, but also will execute the recipe
    //IF set timer is true, and the recipe would return true, the chambers timer will be set
    public boolean testRecipe(ChamberCoreBlockEntity chamber, boolean fullSimulate, boolean setTimer){
        //Get input items
        IItemHandler availableItems = chamber.inputInventory();
        if (availableItems == null)
            return false;

        //Check if pressure is high enough and enough steam is in the chamber
        int chamberPressure = chamber.getPressure();
       if((combustion && chamber.combustionTimer < ticksTaken && !fullSimulate) || (!combustion && !(chamberPressure >= SteamPressure && chamber.Tank().getFluid().getAmount() >= ticksTaken))) {
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
    public RecipeSerializer<?> getSerializer() {
        return AllRecipes.PRESSURE_CHAMBER.SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AllRecipes.PRESSURE_CHAMBER.TYPE.get();
    }

    //Serializing
    public static class PressureChamberRecipeSerializer implements RecipeSerializer<PressureChamberRecipe> {
        public static final MapCodec<PressureChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                INT.fieldOf("pressure").forGetter(PressureChamberRecipe::getSteamPressure),
                FLOAT.fieldOf("laser_heat").forGetter(PressureChamberRecipe::getLaserHeat),
                FLOAT.fieldOf("heat_added").forGetter(PressureChamberRecipe::getHeatAdded),
                INT.fieldOf("ticks_taken").forGetter(PressureChamberRecipe::getTicksTaken),
                INT.fieldOf("heat_rating").forGetter(PressureChamberRecipe::getMinimumHeatRating),
                Ingredient.LIST_CODEC.fieldOf("inputs").forGetter(PressureChamberRecipe::getInputs),
                ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(PressureChamberRecipe::getOutputs),
                BOOL.fieldOf("combustion").forGetter(PressureChamberRecipe::isCombustion)
            ).apply(inst, PressureChamberRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PressureChamberRecipe> STREAM_CODEC =
            OverheatedCodecs.fatComposite(
                    ByteBufCodecs.INT, PressureChamberRecipe::getSteamPressure,
                    ByteBufCodecs.FLOAT,PressureChamberRecipe::getLaserHeat,
                    ByteBufCodecs.FLOAT,PressureChamberRecipe::getHeatAdded,
                    ByteBufCodecs.INT, PressureChamberRecipe::getTicksTaken,
                    ByteBufCodecs.INT, PressureChamberRecipe::getMinimumHeatRating,
                    OverheatedCodecs.STREAM_ING_LIST, PressureChamberRecipe::getInputs,
                    OverheatedCodecs.STREAM_STACK_LIST, PressureChamberRecipe::getOutputs,
                    ByteBufCodecs.BOOL, PressureChamberRecipe::isCombustion,
                    PressureChamberRecipe::new
            );

        // Return our map codec.
        @Override
        public MapCodec<PressureChamberRecipe> codec() {
            return CODEC;
        }

        // Return our stream codec.
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PressureChamberRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    /*
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
                    outputs,
                    GsonHelper.getAsBoolean(SerializedRecipe,"combustion"));

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
            10    Combustion

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
                    inputs,outputs,buf.readBoolean());
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
            buf.writeBoolean(recipe.combustion);
        }

        @Override
        public MapCodec<PressureChamberRecipe> codec() {
            return null;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PressureChamberRecipe> streamCodec() {
            return null;
        }
    }

     */


    //Dummy Methods
    @Override
    public boolean matches(DummyRecipeInput pressureChamberInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(DummyRecipeInput dummyRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }



}
