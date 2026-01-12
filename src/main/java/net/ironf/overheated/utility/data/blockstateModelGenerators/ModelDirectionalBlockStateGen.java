package net.ironf.overheated.utility.data.blockstateModelGenerators;

import net.ironf.overheated.utility.data.BSGen;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModelDirectionalBlockStateGen extends ModelBlockStateGen {

    public ModelDirectionalBlockStateGen(BSGen bsGen) {
        super(bsGen);
    }

    protected int getXRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (facing == Direction.UP){
            return 270;
        } else if (facing == Direction.DOWN){
            return 90;
        }
        return 0;
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
