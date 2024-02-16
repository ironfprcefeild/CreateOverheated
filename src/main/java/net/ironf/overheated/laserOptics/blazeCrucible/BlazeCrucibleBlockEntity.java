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

    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat) {
        timeHeated = 15;
        heatLevel = beamHeat.useUpToOverHeat();
        updateBlockState();
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (timeHeated > 0) {
            this.timeHeated--;
        } else {
            heatLevel = 0;
            updateBlockState();
        }
    }

    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }

    protected void setBlockHeat(BlazeBurnerBlock.HeatLevel heat) {
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
        tag.putInt("timeHeated",this.timeHeated);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        this.timeHeated = tag.getInt("timeHeated");
    }

    public static void addToBoilerHeaters(){
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


}
