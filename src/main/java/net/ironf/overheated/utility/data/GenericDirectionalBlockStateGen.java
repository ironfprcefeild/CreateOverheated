package net.ironf.overheated.utility.data;

import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ModelFile;

public class GenericDirectionalBlockStateGen extends GenericBlockStateGen {

    public GenericDirectionalBlockStateGen(BSGen bsGen) {
        super(bsGen);
    }

    protected int getXRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return facing == Direction.UP ? 0 : (facing == Direction.DOWN ? 180 : 270);
    }

    protected int getYRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return facing.getAxis().isVertical() ? 180 : this.horizontalAngle(facing);
    }

}
