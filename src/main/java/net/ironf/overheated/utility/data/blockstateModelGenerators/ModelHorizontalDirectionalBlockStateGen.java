package net.ironf.overheated.utility.data.blockstateModelGenerators;

import net.ironf.overheated.utility.data.BSGen;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModelHorizontalDirectionalBlockStateGen extends ModelDirectionalBlockStateGen{
    public ModelHorizontalDirectionalBlockStateGen(BSGen bsGen) {
        super(bsGen);
    }

    protected int getXRotation(BlockState state) {
        return 0;
    }


    protected int getYRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return switch (facing){
            case EAST: yield 90;
            case WEST: yield 270;
            case SOUTH: yield 180;
            case DOWN, UP,NORTH:yield 0;
        };
    }


}
