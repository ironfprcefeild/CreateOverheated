package net.ironf.overheated.steamworks.blocks.pressureChamber.core;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.AllTags;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionRecipe;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionVentBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.machines.LaserMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class ChamberCoreBlockEntity extends LaserMachineBlockEntity implements IHaveGoggleInformation {
    public ChamberCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /// Handling
    @Override
    public int getFluidCapacity() {
        return 10000;
    }

    @Override
    public int getInventoryCount() {
        return 2;
    }

    @Override
    public int getItemStackCapacity() {
        return 16;
    }

    @Override
    public int itemOrdinalForSide(Direction side) {
        return switch (side){
            case UP -> 0; //input handler
            case DOWN -> 1; //output handler
            default -> -1; // none
        };
    }

    public IItemHandler outputInventory() {
        return getInventory(1);
    }

    public IItemHandler inputInventory() {
        return getInventory(0);
    }



    /*
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.UP)
                return InputLazyItemHandler.cast();
            else if (side == Direction.DOWN) {
                return OutputLazyItemHandler.cast();
            }
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER){
            return InputLazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

     */

    //Add items to the chamber, or simulate it, returning true if it worked.
    public boolean acceptOutputs(List<ItemStack> recipeOutputItems, boolean simulate) {
        //Loops through items tacks
        for (ItemStack itemStack : recipeOutputItems) {
            if (!ItemHandlerHelper.insertItemStacked(outputInventory(), itemStack.copy(), simulate).isEmpty()) {
                return false;
            }
        }
        return true;
    }



    public int validTimer = 10;
    public int recipeTimer = 0;

    public int combustionTimer = 0;
    public boolean regulateCombustion = false;
    public boolean buildCombustion = false;
    public ArrayList<BlockPos> ventLocations = new ArrayList<>();

    //The total heat coming from inputted lasers
    public int currentPressure = 0;
    public int laserTimer = 0;
    //Doing stuff
    @Override
    public void tick() {
        super.tick();

        //Burn through steam. The method also updates current pressure
        handleSteam();


        //Validity Check & Start new recipe if needed
        if (validTimer-- <= 0){
            validTimer = 40;

            if (checkForValidity()) {
                if (recipeTimer == 0) {
                    startNewRecipe();
                }
            } else {
                //Cancel recipe if invalid
                cancelRecipe();
            }
            // Handle combustion
            handleCombustion();
        }


        //Handle Heat
        if ((currentTemp <= 0 ? 0 : currentTemp)
                * Math.max(1,currentPressure)
                * Math.max(1,Math.min(combustionTimer,4)) > (1024)){
            causeExplode();
        }

        //Recipe Timer

        //1 indicates the recipe is done, so call finish recipe
        if (recipeTimer == 1){
            finishRecipe();
        }
        if (recipeTimer > 0){
            //Decrement recipe timer
            recipeTimer--;
        }
    }

    @Override
    public boolean doCooling() {
        return true;
    }

    private void cancelRecipe() {
        recipeTimer = 0;
        currentRecipe = null;

    }

    private void handleSteam(){
        currentPressure = AllSteamFluids.getSteamPressure(Tank().getFluid());
        if (currentPressure > 0) {
            Tank().drain(1, IFluidHandler.FluidAction.EXECUTE);
        }
        if (combustionTimer > 0){
            combustionTimer--;
            if (regulateCombustion && combustionTimer == 0){
                handleCombustion();
            }
            if (combustionTimer == 0 && recipeTimer > 0){
                level.getRecipeManager().byKey(currentRecipe).ifPresent(
                        recipe -> {
                            if (((PressureChamberRecipe) (recipe.value())).isCombustion()){
                                cancelRecipe();
                            }
                        });
            }
        }
    }

    public void handleCombustion(){

        if ((!regulateCombustion || combustionTimer == 0) && ventLocations.size() == 2){

            //Do some combustion
            FluidStack inputA = getFromCombustionVent(ventLocations.get(0));
            FluidStack inputB = getFromCombustionVent(ventLocations.get(1));
            if (inputA == FluidStack.EMPTY || inputB == FluidStack.EMPTY){
                return;
            }
            isSwapped = true;
            for (RecipeHolder<CombustionRecipe> recipe : level.getRecipeManager().getAllRecipesFor(AllRecipes.COMBUSTION.TYPE.get())){
                if ((testCombustion(inputA,inputB,recipe.value()) || testCombustion(inputB,inputA,recipe.value()))
                    && Tank().fill(recipe.value().getOutputFluid(), IFluidHandler.FluidAction.SIMULATE) == recipe.value().getOutputFluid().getAmount()
                ) {
                    //We're good to go
                    if (buildCombustion){
                        combustionTimer += recipe.value().getCombustionTime();
                    } else {
                        Tank().fill(recipe.value().getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);
                        combustionTimer = recipe.value().getCombustionTime();
                    }
                    CombustionVentBlockEntity ventA = (CombustionVentBlockEntity) level.getBlockEntity(ventLocations.get(0));
                    CombustionVentBlockEntity ventB = (CombustionVentBlockEntity) level.getBlockEntity(ventLocations.get(1));

                    if (isSwapped){
                        ventB.Tank().drain(recipe.value().getInputFluidB().getStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
                        ventA.Tank().drain(recipe.value().getInputFluidA().getStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    } else {
                        ventA.Tank().drain(recipe.value().getInputFluidA().getStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
                        ventB.Tank().drain(recipe.value().getInputFluidB().getStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    }

                    currentPressure = 0;

                    addHeat((float) (double) (recipe.value().getOutputFluid().getAmount() * (recipe.value().getLaserHeat()+1) / 16));
                }
            }
        }
    }
    boolean isSwapped = true;
    public boolean testCombustion(FluidStack a, FluidStack b, CombustionRecipe r){
        isSwapped = !isSwapped;
        return (r.getInputFluidA().test(a) && r.getInputFluidB().test(b)) &&
                (getHeatRating() >= r.getHeatRating() && getHeatRating() >= r.getLaserHeat());
    }

    public FluidStack getFromCombustionVent(BlockPos pos){
        if (level.getBlockEntity(pos) instanceof CombustionVentBlockEntity CVBE){
            return CVBE.Tank().getFluid();
        } else {
            return FluidStack.EMPTY;
        }
    }

    private void finishRecipe() {
        if (currentRecipe == null)
            return;
        level.getRecipeManager().byKey(currentRecipe).ifPresent(
                recipe -> {
                    ((PressureChamberRecipe) (recipe.value())).testRecipe(this, false, false);
                    currentRecipe = null;});
        //Trigger another validity check, possible starting another recipe if the recipe delay is shorter than the validity check delay
        validTimer = 0;
    }

    public ResourceLocation currentRecipe;
    private void startNewRecipe() {
        for (RecipeHolder<PressureChamberRecipe> r : level.getRecipeManager().getAllRecipesFor(AllRecipes.PRESSURE_CHAMBER.TYPE.get())){
            if(r.value().testRecipe(this,true,true)){
                currentRecipe = r.id();
                return;
            }
        }
    }

    private boolean checkForValidity() {
        int weakConnections = 0;
        regulateCombustion = false;
        buildCombustion = false;
        ventLocations.clear();

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos lookAt = getBlockPos().offset(x, y, z);
                    BlockState state = level.getBlockState(lookAt);
                    if (AllBlocks.COMBUSTION_VENT.has(state)){
                        ventLocations.add(lookAt);
                    } else if (AllBlocks.COMBUSTION_REGULATOR.has(state)){
                        regulateCombustion = true;
                    } else if (AllBlocks.COMBUSTION_ENGINE.has(state)) {
                        buildCombustion = true;
                    } else if (AllTags.AllBlockTags.WEAK_CHAMBER_BORDER.matches(state)){
                        weakConnections++;
                    } else if (!AllTags.AllBlockTags.CHAMBER_BORDER.matches(state)) {
                        //Overheated.LOGGER.info(lookAt.toShortString());
                        return false;
                    }
                }
            }
        }
        return weakConnections <= 6 && (ventLocations.size() == 2 || ventLocations.isEmpty());
    }

    private void causeExplode() {
        BlockPos pos = getBlockPos();
        currentPressure = 1 + AllSteamFluids.getSteamPressure(Tank().getFluid());
        level.explode(null,pos.getX(),pos.getY(),pos.getZ(),5f * currentPressure, Level.ExplosionInteraction.TNT);
    }

    public void addHeat(float heatAdded) {
        currentTemp += heatAdded;
    }
    public void setTimer(int ticksTaken) {
        recipeTimer = ticksTaken;
    }
    public void drainCombustion(int timeDrained){
        combustionTimer = Math.max(combustionTimer-timeDrained,0);
    }

    public float getHeating() {
        return totalLaserHeat.getTotalHeat();
    }

    public int getHeatRating(){
        return totalLaserHeat.OverHeat >= 1 ? 3 : (totalLaserHeat.SuperHeat >= 1 ? 2 : 1);
    }

    public HeatData getLaserHeat() {
        return totalLaserHeat;
    }

    public int getPressure() {
        return currentPressure;
    }

    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip,isPlayerSneaking);
        if (checkForValidity()) {
            GoggleHelper.heatTooltip(tooltip, totalLaserHeat, HeatDisplayType.ABSORB);

            tempAndCoolInfo(tooltip);
            tooltip.add(addIndent(Component.translatable("coverheated.pressure_chamber.explode")));
            tooltip.add(addIndent(Component.literal(String.valueOf(Math.ceil((double) 1024 / (Math.max(1,currentPressure) * Math.max(1,Math.min(combustionTimer,4)))))),1));

            if (combustionTimer > 0) {
                tooltip.add(addIndent(Component.translatable("coverheated.pressure_chamber.combustion_left")));
                tooltip.add(addIndent(Component.literal(String.valueOf(Math.ceil((double) combustionTimer / 20))), 1));
            }

            if (currentRecipe != null) {
                tooltip.add(addIndent(Component.translatable("coverheated.pressure_chamber.time_left")));
                tooltip.add(addIndent(Component.literal(String.valueOf(Math.ceil((double) recipeTimer / 20))),1));
            } else {
                tooltip.add(addIndent(Component.translatable("coverheated.pressure_chamber.no_recipe")));

            }

        } else {
            //Chamber Invalid
            tooltip.add(addIndent(Component.translatable("coverheated.pressure_chamber.invalid")));

        }
        return true;
    }


    //Bookkeeping

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        validTimer = tag.getInt("validTimer");
        recipeTimer = tag.getInt("recipeTimer");
        currentPressure = tag.getInt("pressure");
        laserTimer = tag.getInt("laserTimer");
        currentRecipe = !tag.getString("recipe").equals("null") ? ResourceLocation.tryParse(tag.getString("recipe")) : null;
        isSwapped = tag.getBoolean("combustionSwapped");
        regulateCombustion = tag.getBoolean("regulateCombustion");
        buildCombustion = tag.getBoolean("buildCombustion");
        ventLocations.clear();
        if (tag.getBoolean("validVents")){
            ventLocations.add(BlockPos.of(tag.getLong("venta")));
            ventLocations.add(BlockPos.of(tag.getLong("ventb")));
        }

    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("validTimer",validTimer);
        tag.putInt("recipeTimer",recipeTimer);
        tag.putInt("pressure",currentPressure);
        tag.putInt("laserTimer",laserTimer);
        tag.putString("recipe",currentRecipe != null ? currentRecipe.getPath() : "null");
        tag.putBoolean("combustionSwapped",isSwapped);
        tag.putBoolean("regulateCombustion",regulateCombustion);
        tag.putBoolean("buildCombustion",buildCombustion);

        if (ventLocations.size() != 2){
            tag.putBoolean("validVents",false);
        } else {
            tag.putBoolean("validVents",true);
            tag.putLong("venta",ventLocations.get(0).asLong());
            tag.putLong("ventb",ventLocations.get(1).asLong());
        }
    }
}
