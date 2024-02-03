package net.ironf.overheated.laserOptics.backend;

import com.mojang.datafixers.TypeRewriteRule;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.backend.beam.BeamBlockEntity;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

//This class indicates block-entities which create laser beams,
public abstract class LaserGenerator extends SmartBlockEntity {

    int breakProgress = 0;
    public LaserGenerator(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //This method must be overrided
    //This method is used to find out what laser beam should be created, via the heat data class. Returning null will indicate not to make a laser beam.
    //This method will be called once for every direction every tick, the tested direction is passed into the method via the dir parameter.
    public HeatData laserToPush(Direction dir){
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        for (Direction dir : Iterate.directions){
            emitLaser(dir);
        }
    }

    //This method places a beam block. Consider how the beam inherits this class, this means it shoots off.
    public void emitLaser(Direction dir) {
        //Laser should pass through blocks until reaching a non-transparent block, at each block it checks for a blaze burner to ignite or a laser absorber to stop at
        HeatData toSend = laserToPush(dir);
        int offset = 1;
        BlockPos pos = getBlockPos();

        //Doing stuff, incrementing offset indicates that we are doing something (or nothing) then moving to the next block this tick. This continues until laser is placed.
        // (though sometimes absorbed, tries to break something, or runs out of hea)
        while (true){
            if (toSend.getTotalHeat() < 1){
                //No more heat, end beam
                break;
            }
            BlockPos testAt = pos.relative(dir,offset);
            BlockState toTest = level.getBlockState(testAt);
            if (Blocks.AIR.defaultBlockState() == toTest) {
                //Place a Beam, set its heat
                level.setBlock(testAt, net.ironf.overheated.AllBlocks.BEAM.getDefaultState(), 2);
                ((BeamBlockEntity) level.getBlockEntity(testAt)).setHeat(toSend);
                break;
            } else if (AllBlocks.BEAM.has(toTest)){
                //If you hit a beam just update its heat if it faces the same direction, otherwise, make a laser cyst
                if (toTest.getValue(BlockStateProperties.FACING) == dir) {
                    ((BeamBlockEntity) level.getBlockEntity(testAt)).setHeat(toSend);
                } else {
                    level.setBlock(testAt, AllBlocks.LASER_CYST.getDefaultState(),2);
                }
                break;
            } else if (net.ironf.overheated.AllBlocks.BLAZE_CRUCIBLE.has(toTest)) {
                //Heat crucible with the highest level available, the useUpToOverHeat method will tell us the heat level it used, and then use 1 of that level
                ((BlazeCrucibleBlockEntity) level.getBlockEntity(testAt)).heat(20, toSend.useUpToOverHeat());
                markRender(testAt, dir);
                offset++;
            }
            else if (level.getBlockEntity(testAt) instanceof LaserAbsorber) {
                //Call absorb on the laser absorber, then do nothing
                ((LaserAbsorber) level.getBlockEntity(testAt)).absorb(toSend,dir.getOpposite());
                break;

            } else if (isTransparent(toTest)) {
                //Pass through, mark as extra renderering
                markRender(testAt,dir);
                offset++;
            }
            else {
                //Try Break the Block, if it's a strong block this may take time and thus passes to the next tick
                if (breakProgress < getExplosionResistance(toTest) * 10){
                    breakProgress += toSend.Volatility;
                } else {
                    breakProgress = 0;
                    level.removeBlock(testAt,true);
                }
                break;
            }
        }
    }
    //This method should be used to render additional parts of the beam in transparent blocks

    private void markRender(BlockPos testAt, Direction dir) {
    }


    public boolean isTransparent(BlockState toTest) {
        return false;
    }

    public int getExplosionResistance(BlockState toTest){
        return (int) toTest.getBlock().getExplosionResistance();
    }

    //TODO figure out how these property getters work
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tag.putInt("progress",this.breakProgress);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        this.breakProgress = tag.getInt("progress");
    }
}
