package net.ironf.overheated.cooling.chillChannel.core;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.createmod.catnip.outliner.Outliner;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.chillChannel.ChannelBlockEntity;
import net.ironf.overheated.cooling.chillChannel.MutableDirection;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChannelCoreBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public ChannelCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int tickTimer = 20;
    public float currentEff = 0f;
    public float currentMinTemp = 0f;
    public int flywheelPower = 0;
    public ChannelStatusBundle coolingUnits = new ChannelStatusBundle();
    public boolean active = true;
    BlockPos highlightError;
    String errorMessage = "";

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("timer");
        currentEff = tag.getFloat("currenteff");
        currentMinTemp = tag.getFloat("currentmintemp");
        flywheelPower = tag.getInt("flywheelpower");
        coolingUnits = new ChannelStatusBundle(tag,"status");
        active = tag.getBoolean("active");
        errorMessage = tag.getString("error");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",tickTimer);
        tag.putFloat("currenteff",currentEff);
        tag.putFloat("currentmintemp",currentMinTemp);
        tag.putInt("flywheelpower",flywheelPower);
        coolingUnits.write(tag,"status");
        tag.putBoolean("active",active);
        tag.putString("error",errorMessage);
    }

    @Override
    public void tick() {
        super.tick();
        //Every Tick Timer, trigger this.
        //If core is invalid, next attempt will come sooner, otherwise there is a full minute delay
        if (tickTimer-- == 0){
            updateValidity();
            tickTimer = active ? 80 : 20;
            if (active){
                //We are active, so we can drain coolant
                tank.getPrimaryHandler().drain((flywheelPower / 10), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public void updateValidity(){
        //Update Flywheel Power
        MutableDirection channelMovingIn = new MutableDirection(level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING));
        Direction flyWheelsIn = channelMovingIn.getImmutable().getOpposite();
        flywheelPower = 0;
        int flyWheel = 0;
        while (flyWheel <= 8){
            flyWheel++;
            if (level.getBlockEntity(getBlockPos().relative(flyWheelsIn,flyWheel)) instanceof FlywheelBlockEntity FWBE){
                flywheelPower += (int) Math.abs(FWBE.getSpeed());
            } else {
                break;
            }
        }
        if (flywheelPower == 0){
            disable("no_flywheels");
            return;
        }

        //Validate Coolant
        Fluid fluidContained = tank.getPrimaryHandler().getFluid().getFluid();
        if (!CoolingHandler.minTempHandler.containsKey(fluidContained) || tank.getPrimaryHandler().getFluidAmount() < flywheelPower){
            disable("no_coolant");
            return;
        }

        //Find Min Temp and Efficiency
        float minTemp = CoolingHandler.minTempHandler.get(fluidContained);
        float networkEff = CoolingHandler.efficiencyHandler.get(fluidContained);

        //Prep Network Info
        coolingUnits.reset();
        int maxChannels = flywheelPower;

        //Find the Position of the first Channel block
        BlockPos currentPos = getBlockPos().relative(channelMovingIn.getImmutable());
        ArrayList<BlockPos> channelNodes = new ArrayList<>();

        //Loop, moving along channels
        while (maxChannels > 0){
            maxChannels--;
            if (level.getBlockEntity(currentPos) instanceof ChannelBlockEntity CBE){
                channelNodes.add(currentPos);
                currentPos = CBE.propagateChannel(coolingUnits,networkEff,minTemp,channelMovingIn);
                if (currentPos == null || !level.isInWorldBounds(currentPos)){
                    //Failure!
                    Overheated.LOGGER.info("Error 1");
                    disable("incomplete_loop");
                    break;
                }
            } else if (compareBlockPos(currentPos,getBlockPos())){
                //This means we have finished the loop!
                this.active = coolingUnits.getDelta() >= 0;
                if (!this.active){
                    errorMessage = "not_enough_sources";
                } else {
                    errorMessage = "";
                }
                this.currentEff = networkEff;
                this.currentMinTemp = minTemp;
                break;
            } else {
                Overheated.LOGGER.info("Error 2");
                highlightError = currentPos;
                disable("incomplete_loop");
                break;
            }
        }

        if (active){
            for (BlockPos bp : channelNodes){
                ((ChannelBlockEntity) level.getBlockEntity(bp)).acceptNetwork();
            }
        }
    }

    public void disable(String message){
        errorMessage = message;
        active = false;
        coolingUnits = new ChannelStatusBundle();
        coolingUnits.maximumCooling = 0;
        coolingUnits.usedCooling = 0;
    }

    public boolean compareBlockPos(BlockPos a, BlockPos b){
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }
    ///Fluid Handling
    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 8000).allowExtraction().allowInsertion());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }



    ///Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (errorMessage != "") {
            tooltip.add(GoggleHelper.addIndent(
                    Component.translatable("coverheated.chill_channel.error." + errorMessage)));
            if (highlightError != null) {
                Outliner.getInstance().showAABB(this, new AABB(highlightError), 200);
            }

        }
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.network_status").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(coolingUnits.usedCooling) + "/" + GoggleHelper.easyFloat(coolingUnits.maximumCooling)).withStyle(coolingUnits.getDelta() >= 0 ? ChatFormatting.AQUA : ChatFormatting.RED),1));
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.mintemp").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(currentMinTemp)).withStyle(ChatFormatting.AQUA),1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.eff").append(String.valueOf(currentEff))));



        return true;
    }
}
