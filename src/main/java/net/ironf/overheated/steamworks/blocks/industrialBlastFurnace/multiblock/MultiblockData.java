package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock;

import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.BlastFurnaceServantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Iterator;

import static net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock.BlastFurnaceMultiblock.isAir;

public class MultiblockData {

    public BlockPos minPos;
    public BlockPos maxPos;

    public BlockPos minInsidePos;
    public BlockPos maxInsidePos;

    public AABB bounds;

    public BlastFurnaceControllerBlockEntity controller;

    public final ArrayList<BlockPos> servantPositions;

    public MultiblockData(BlockPos minPos, BlockPos maxPos, BlastFurnaceControllerBlockEntity controller, ArrayList<BlockPos> servantPositions){
        this.controller = controller;
        BlockPos controllerPosition = controller.getBlockPos();
        Level level = controller.getLevel();

        this.servantPositions = servantPositions;

        this.maxPos = maxPos;
        this.minPos = minPos;

        minInsidePos = minPos.offset(1,  1,1);
        maxInsidePos = maxPos.offset(-1,0,-1);

        bounds = new AABB(minInsidePos,maxInsidePos.offset(1,1,1));

        for (BlockPos bp : servantPositions){
            BlastFurnaceServantBlockEntity be = (BlastFurnaceServantBlockEntity) level.getBlockEntity(bp);
            be.updateController(controllerPosition);
        }
    }

    //Returns the total inner area of the inside blocks
    public int innerArea(){
        return (maxInsidePos.getX() - minInsidePos.getX()) * (maxInsidePos.getZ() - minInsidePos.getZ()) * (maxInsidePos.getY() - minInsidePos.getY());
    }

    public boolean isPosWithin(BlockPos pos){
        return bounds.contains(pos.getCenter());
    }

    //Read Write stuff, this is also simplifier to tinker's implementation
    public void writeTag(CompoundTag tag, String s){
        tag.putLong(s+"minpos",minPos.asLong());
        tag.putLong(s+"maxpos",maxPos.asLong());

        tag.putLong(s+"mininsidepos",minInsidePos.asLong());
        tag.putLong(s+"maxinsidepos",maxInsidePos.asLong());

        tag.putInt(s+"servantarraysize",servantPositions.size());
        for (int i = 0; i < servantPositions.size(); i++) {
            tag.putLong(s+"servantno"+i,servantPositions.get(i).asLong());
        }

    }

    public void readTag(CompoundTag tag, String s, BlastFurnaceControllerBlockEntity controller){
        minPos = BlockPos.of(tag.getLong(s+"minpos"));
        maxPos = BlockPos.of(tag.getLong(s+"maxpos"));

        minInsidePos = BlockPos.of(tag.getLong(s+"mininsidepos"));
        maxInsidePos = BlockPos.of(tag.getLong(s+"maxinsidepos"));

        bounds = new AABB(minInsidePos,maxInsidePos.offset(1,1,1));

        this.controller = controller;

        int servantListSize = tag.getInt(s+"servantarraysize");
        for (int i = 0; i < servantListSize; i++) {
            servantPositions.add(BlockPos.of(tag.getLong(s+"servantno"+i)));
        }
    }

    public Iterator<BlockPos> getOutGasPositions(Level level) {
        ArrayList<BlockPos> readyPos = new ArrayList<>();
        BlockPos from = minInsidePos.above();
        BlockPos to = maxInsidePos.above();

       int y = from.getY();
        for (int x = from.getX() + 1; x < to.getX(); x++) {
            for (int z = from.getZ() + 1; z < to.getZ(); z++) {
                if (isAir(level, new BlockPos(x, y, z))) {
                    readyPos.add(new BlockPos(x, y, z));
                }
            }
        }

        return readyPos.iterator();
    }
}
