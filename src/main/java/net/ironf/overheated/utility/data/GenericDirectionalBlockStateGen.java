package net.ironf.overheated.utility.data;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GenericDirectionalBlockStateGen extends GenericBlockStateGen {

    public GenericDirectionalBlockStateGen(BSGen bsGen) {
        super(bsGen);
    }

    protected int getXRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return facing == Direction.DOWN ? 180 : 0;
    }

    protected int getYRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing){
            case EAST: yield 90;
            case WEST: yield 270;
            case SOUTH: yield 180;
            case DOWN, UP, NORTH: yield 0;
        };
    }

}
