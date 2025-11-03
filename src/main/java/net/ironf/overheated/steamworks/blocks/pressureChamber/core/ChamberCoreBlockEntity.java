package net.ironf.overheated.steamworks.blocks.pressureChamber.core;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllTags;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.SmartLaserMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class ChamberCoreBlockEntity extends SmartLaserMachineBlockEntity implements IHaveGoggleInformation {
    public ChamberCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }



    //Item & Fluid Handling

    public LazyOptional<IFluidHandler> InputLazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour InputTank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(InputTank = SmartFluidTankBehaviour.single(this, 10000));
    }
    private LazyOptional<IItemHandler> InputLazyItemHandler = LazyOptional.empty();
    private final ItemStackHandler InputItemHandler = new ItemStackHandler(16) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> OutputLazyItemHandler = LazyOptional.empty();
    private final ItemStackHandler OutputItemHandler = new ItemStackHandler(16) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public IItemHandler getInputItems() {
        return InputItemHandler;
    }
    @Override
    public void onLoad() {
        super.onLoad();
        InputLazyItemHandler = LazyOptional.of(() -> InputItemHandler);
        OutputLazyItemHandler = LazyOptional.of(() -> OutputItemHandler);
        InputLazyFluidHandler = LazyOptional.of(() -> InputTank.getPrimaryHandler());
    }
    @Override
    public void invalidate() {
        super.invalidate();
        InputLazyItemHandler.invalidate();
        OutputLazyItemHandler.invalidate();
        InputLazyFluidHandler.invalidate();
    }
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

    //Add items to the chamber, or simulate it, returning true if it worked.
    public boolean acceptOutputs(List<ItemStack> recipeOutputItems, boolean simulate) {
        //Loops through items tacks
        for (ItemStack itemStack : recipeOutputItems) {
            if (!ItemHandlerHelper.insertItemStacked(OutputItemHandler, itemStack.copy(), simulate).isEmpty()) {
                return false;
            }
        }
        return true;
    }


    public int validTimer = 10;
    public int recipeTimer = 0;
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
            validTimer = 50;
            if (checkForValidity()) {
                if (recipeTimer == 0) {
                    startNewRecipe();
                }
            } else {
                //Cancel recipe if invalid
                cancelRecipe();
            }
        }


        //Handle Heat
        if ((currentTemp <= 0 ? 0 : currentTemp) * Math.max(1,currentPressure) > 1024){
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
        currentPressure = AllSteamFluids.getSteamPressure(InputTank.getPrimaryHandler().getFluid());
        InputTank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
    }

    private void finishRecipe() {
        if (currentRecipe == null)
            return;
        level.getRecipeManager().byKey(currentRecipe).ifPresent(
                recipe -> {
                    ((PressureChamberRecipe) recipe).testRecipe(this, false, false);
                    currentRecipe = null;});
        //Trigger another validity check, possible starting another recipe if the recipe delay is shorter than the validity check delay
        validTimer = 0;
    }

    public ResourceLocation currentRecipe;
    private void startNewRecipe() {
        for (PressureChamberRecipe r : level.getRecipeManager().getAllRecipesFor(PressureChamberRecipe.Type.INSTANCE)){
            if(r.testRecipe(this,true,true)){
                currentRecipe = r.getId();
                return;
            }
        }
    }

    private boolean checkForValidity() {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos lookAt = getBlockPos().offset(x, y, z);
                    BlockState state = level.getBlockState(lookAt);
                    if (!AllTags.AllBlockTags.CHAMBER_BORDER.matches(state)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void causeExplode() {
        BlockPos pos = getBlockPos();
        currentPressure = AllSteamFluids.getSteamPressure(InputTank.getPrimaryHandler().getFluid());
        level.explode(null,pos.getX(),pos.getY(),pos.getZ(),6f * currentPressure, Level.ExplosionInteraction.TNT);
    }

    public void addHeat(float heatAdded) {
        currentTemp += heatAdded;
    }
    public void setTimer(int ticksTaken) {
        recipeTimer = ticksTaken;
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
        if (checkForValidity()) {
            containedFluidTooltip(tooltip, isPlayerSneaking, InputLazyFluidHandler);

            GoggleHelper.heatTooltip(tooltip, totalLaserHeat, HeatDisplayType.ABSORB);

            tempAndCoolInfo(tooltip);
            tooltip.add(addIndent(Component.translatable("coverheated.pressure_chamber.explode")));
            tooltip.add(addIndent(Component.literal(String.valueOf(Math.ceil((double) 1024 /Math.max(1,currentPressure)))),1));

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
    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("validTimer",validTimer);
        tag.putInt("recipeTimer",recipeTimer);
        tag.putInt("pressure",currentPressure);
        tag.putInt("laserTimer",laserTimer);
        tag.putString("recipe",currentRecipe != null ? currentRecipe.getPath() : "null");


    }
}
