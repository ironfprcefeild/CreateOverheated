package net.ironf.overheated.utility.data;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GenericSpunDirectionalBlockStateGen extends GenericDirectionalBlockStateGen {

    public GenericSpunDirectionalBlockStateGen(BSGen bsGen) {
        super(bsGen);
    }

    @Override
    protected int getXRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing){
            case EAST, WEST, NORTH, SOUTH: yield 0;
            case DOWN: yield 270;
            case UP: yield 90;
        };
    }


}
