package net.ironf.overheated.laserOptics.backend.beam;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.backend.LaserGenerator;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class BeamBlockEntity extends LaserGenerator {
    public BeamBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        if (isGeneratorBehind()) {
            //Since a beam is behind we can create a laser, this boolean method should update the myHeat value if a beam is behind
            //If its the first beam from a non-beam generator it should be being updated by that generator
            super.tick();
        } else {
            //Destroy self
            level.setBlock(getBlockPos(), Blocks.AIR.defaultBlockState(),2);
        }
    }

    public boolean isGeneratorBehind() {
        int offset = 1;
        BlockPos pos = getBlockPos();
        Direction backDir = level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING).getOpposite();
        while (true){
            BlockPos testAt = pos.relative(backDir,offset);
            BlockState toTest = level.getBlockState(testAt);
            if (AllBlocks.BEAM.has(toTest)){
                //A beam is securing, update the heat
                this.setHeat(((BeamBlockEntity) (level.getBlockEntity(testAt))).myHeat);
                return true;
            } else if (level.getBlockEntity(testAt) instanceof LaserGenerator){
                //A laser generator is emiting (and should be updating this) so it's all good
                return true;
            }else if (isTransparent(toTest)) {
                //Pass through
                offset++;
                continue;
            }
            //Nothing is securing, return false to break
            return false;
        }
    }

    HeatData myHeat = HeatData.empty();
    public void setHeat(HeatData h){
        this.myHeat = h;
    }
    @Override
    public HeatData laserToPush(Direction dir) {
        Direction valid = level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING);
        if (dir == valid){
            return myHeat;
        }
        return null;
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.myHeat = HeatData.readTag(tag);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        HeatData.writeTag(tag,this.myHeat);
    }
}
