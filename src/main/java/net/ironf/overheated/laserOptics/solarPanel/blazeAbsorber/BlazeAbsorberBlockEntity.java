package net.ironf.overheated.laserOptics.solarPanel.blazeAbsorber;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;

public class BlazeAbsorberBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public BlazeAbsorberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    float heatAmount;

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heatAmount = tag.getFloat("heat");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat",heatAmount);
    }

    @Override
    public void lazyTick() {
        update();
    }

    //Recalculates the heat value, and updates the block state.
    public void update() {
        heatAmount = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1 ; y++) {
                if (level.getBlockEntity(getBlockPos().offset(x,0,y)) instanceof SolarPanelBlockEntity panel){
                    heatAmount += panel.recentReading;
                }
            }
        }
        setBlockHeat(getHeatLevel());
    }
    public void setBlockHeat(BlazeBurnerBlock.HeatLevel heat) {
        //Get the current BlockState
        BlazeBurnerBlock.HeatLevel oldHeat = BlazeBurnerBlock.getHeatLevelOf(getBlockState());
        //If BlockState does not need updated, return, otherwise update accordingly.
        if (oldHeat == heat) return;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlazeBurnerBlock.HEAT_LEVEL, heat));
        notifyUpdate();
    }
    //Gets the heatlevel the blockstate should be set too.
    protected BlazeBurnerBlock.HeatLevel getHeatLevel() {
        return heatAmount >= 1 ? BlazeBurnerBlock.HeatLevel.KINDLED : BlazeBurnerBlock.HeatLevel.SMOULDERING;
    }

    public static void addToBoilerHeaters(){
        Overheated.LOGGER.info("Adding the Blaze Absorber to Boiler Heaters");
        BoilerHeater.REGISTRY.register(AllBlocks.BLAZE_ABSORBER.get(), (level, pos, state) -> {
            try {
                BlazeAbsorberBlockEntity absorber = ((BlazeAbsorberBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos)));
                return absorber.heatAmount >= 1 ? absorber.heatAmount : 0;
            } catch (NullPointerException e){
                return -1;
            }
        });
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isPlayerSneaking){
            GoggleHelper.heatTooltip(tooltip,"coverheated.absorber.from_panels",new HeatData(heatAmount,0,0), HeatDisplayType.ABSORB,3);
            GoggleHelper.heatTooltip(tooltip,"coverheated.absorber.to_boiler",new HeatData((float) Math.floor(heatAmount),0,0), HeatDisplayType.SUPPLYING,3);

        } else {
            GoggleHelper.heatTooltip(tooltip,new HeatData((float) Math.floor(heatAmount),0,0), HeatDisplayType.SUPPLYING,3);
        }
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
