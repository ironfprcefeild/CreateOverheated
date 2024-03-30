package net.ironf.overheated.laserOptics.blazeCrucible;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

import static com.simibubi.create.content.fluids.tank.BoilerHeaters.registerHeater;

public class BlazeCrucibleBlockEntity extends SmartBlockEntity implements ILaserAbsorber {

    public int timeHeated = 0;
    public int heatLevel = 0;
    public boolean needsStateUpdate = true;

    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat) {
        int newHeat = beamHeat.useUpToOverHeat();
        if (heatLevel != newHeat){
            needsStateUpdate = true;
        }
        heatLevel = newHeat;
        timeHeated = 15;
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (needsStateUpdate){
            updateBlockState();
            needsStateUpdate = false;
        }
        if (timeHeated > 0) {
            this.timeHeated--;
        } else {
            heatLevel = 0;
            needsStateUpdate = true;
        }
    }

    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }

    public void setBlockHeat(BlazeBurnerBlock.HeatLevel heat) {
        BlazeBurnerBlock.HeatLevel inBlockState = getHeatLevelFromBlock();
        if (inBlockState == heat)
            return;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlazeBurnerBlock.HEAT_LEVEL, heat));
        notifyUpdate();
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock() {
        return BlazeBurnerBlock.getHeatLevelOf(getBlockState());
    }

    protected BlazeBurnerBlock.HeatLevel getHeatLevel() {
        BlazeBurnerBlock.HeatLevel level = BlazeBurnerBlock.HeatLevel.SMOULDERING;
        if (timeHeated > 0) {
            if (heatLevel > 1){
                //Superheated
                level = BlazeBurnerBlock.HeatLevel.SEETHING;
            } else {
                //Heated
                level = BlazeBurnerBlock.HeatLevel.KINDLED;
            }
        }
        return level;
    }

    public BlazeCrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.timeHeated = tag.getInt("timeHeated");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timeHeated",this.timeHeated);
        needsStateUpdate = true;
    }

    public static void addToBoilerHeaters(){
        Overheated.LOGGER.info("Adding the Blaze Crucible to Boiler Heaters");
        registerHeater(AllBlocks.BLAZE_CRUCIBLE.getId(), (level, pos, state) -> {
            try {
                BlazeCrucibleBlockEntity crucible = ((BlazeCrucibleBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos)));
                int timeHeated = crucible.timeHeated;
                int heatLevel = crucible.heatLevel;
                if (timeHeated > 0) {
                    return heatLevel;
                }
                return 0;
            } catch (NullPointerException e){
                return 0;
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
        needsStateUpdate = true;
    }
}
