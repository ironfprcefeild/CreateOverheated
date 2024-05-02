package net.ironf.overheated.steamworks.blocks.pressureChamber.core;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.AllTags;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.heatsink.HeatSinkHelper;
import net.ironf.overheated.steamworks.blocks.pressureChamber.PressureChamberRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

public class ChamberCoreBlockEntity extends SmartBlockEntity implements ILaserAbsorber, HeatSinkHelper {
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
    //Iteams are being consume dbut not removed
    //TODO current bug ^^
    public boolean acceptOutputs(List<ItemStack> recipeOutputItems, boolean simulate) {
        //Loops through items tacks
        for (ItemStack itemStack : recipeOutputItems) {
            if (!ItemHandlerHelper.insertItemStacked(OutputItemHandler, itemStack.copy(), simulate).isEmpty())
                return false;
        }
        return true;
    }


//TODO add heat level minimum to recipe and BE
    public int validTimer = 10;
    public int recipeTimer = 0;
    //The number that if it gets too big explodes
    public float chamberHeat = 0;
    //The total heat coming from inputted lasers
    public float currentHeating = 0;
    public int currentHeatRating = 0;
    public int currentPressure = 0;
    public float currentAirflow = 0;
    public int laserTimer = 0;
    //Doing stuff
    @Override
    public void tick() {

        //Burn through steam, if steam could not be transferred do not move valid timer. The method also updates current pressure
        boolean steamUsed = handleSteam();
        //Validity Check & Start new recipe if needed
        if (validTimer-- <= 0 && steamUsed){
            Overheated.LOGGER.info("1");
            validTimer = 50;
            if (checkForValidity()) {
                Overheated.LOGGER.info("2");
                if (recipeTimer == 0) {
                    startNewRecipe();
                }
            } else {
                //Cancel recipe if invalid
                cancelRecipe();
            }
        } else if (!steamUsed){
            //Cancel recipe if no steam
            cancelRecipe();
        }

        //Laser Check
        if (laserTimer > 0){
            laserTimer--;
        } else {
            currentHeatRating = 0;
            currentHeating = 0;
        }

        //Handle Heat
        if (chamberHeat > 1024){
            causeExplode();
        }
        chamberHeat = Math.min(chamberHeat - (currentAirflow / 256), 0);
        //Recipe Timer

        //1 indicates the recipe is done, so call finish recipe
        if (recipeTimer == 1){
            finishRecipe();
        }
        if (recipeTimer > 0){
            //Decrement recipe timer
            Overheated.LOGGER.info(String.valueOf(recipeTimer));
            recipeTimer--;
        }
    }


    //TODO cancelling for no reason
    private void cancelRecipe() {
        recipeTimer = 0;
        currentRecipe = null;
        Overheated.LOGGER.info("Recipe Canceled");
    }

    //Returns true if steam was transferable
    private boolean handleSteam(){
        currentPressure = AllSteamFluids.getSteamPressure(InputTank.getPrimaryHandler().getFluid());
        Overheated.LOGGER.info("Pressure" + currentPressure);
        if (currentPressure >= 0 && InputTank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1){
            InputTank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    private void finishRecipe() {
        Overheated.LOGGER.info("Finishing Recipe");
        if (currentRecipe == null)
            return;
        level.getRecipeManager().byKey(currentRecipe).ifPresent(
                recipe -> {
                    ((PressureChamberRecipe) recipe).testRecipe(this, false, false);
                    currentRecipe = null;});
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
        currentAirflow = 0;
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos lookAt = getBlockPos().offset(x, y, z);
                    BlockState state = level.getBlockState(lookAt);
                    if (!AllTags.AllBlockTags.CHAMBER_BORDER.matches(state))
                        return false;
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1 && state == AllBlocks.CHAMBER_HEAT_SINK.getDefaultState()){
                        for (Direction d : Iterate.directions){
                            currentAirflow += getHeatSunkenFrom(lookAt.relative(d),level);
                        }
                        continue;
                    }
                }
            }
        }
        return true;
    }

    private void causeExplode() {
        BlockPos pos = getBlockPos();
        level.explode(null,pos.getX(),pos.getY(),pos.getZ(),15f, Level.ExplosionInteraction.TNT);
    }

    public void addHeat(float heatAdded) {
        chamberHeat += heatAdded;
    }
    public void setTimer(int ticksTaken) {
        recipeTimer = ticksTaken;
    }


    //TODO make these methods work
    public float getHeating() {
        return currentHeating;
    }

    public int getHeatRating(){
        return currentHeatRating;
    }

    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat) {
        currentHeatRating = beamHeat.OverHeat >= 1 ? 3 : (beamHeat.SuperHeat >= 1 ? 2 : 1);
        currentHeating = beamHeat.getTotalHeat();
        laserTimer = 60;
        return false;
    }

    public int getPressure() {
        return currentPressure;
    }



    //Bookkeeping

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        validTimer = tag.getInt("validTimer");
        recipeTimer = tag.getInt("recipeTimer");
        currentHeating = tag.getFloat("heat");
        currentHeatRating = tag.getInt("heatRating");
        chamberHeat = tag.getFloat("chamberHeat");
        currentPressure = tag.getInt("pressure");
        laserTimer = tag.getInt("laserTimer");
        currentAirflow = tag.getFloat("currentAirflow");
        currentRecipe = !tag.getString("recipe").equals("null") ? ResourceLocation.tryParse(tag.getString("recipe")) : null;
    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("validTimer",validTimer);
        tag.putInt("recipeTimer",recipeTimer);
        tag.putFloat("heat", currentHeating);
        tag.putFloat("chamberHeat",chamberHeat);
        tag.putInt("pressure",currentPressure);
        tag.putInt("laserTimer",laserTimer);
        tag.putInt("heatRating",currentHeatRating);
        tag.putFloat("currentAirflow",currentAirflow);
        tag.putString("recipe",currentRecipe != null ? currentRecipe.getPath() : "null");

    }
}
