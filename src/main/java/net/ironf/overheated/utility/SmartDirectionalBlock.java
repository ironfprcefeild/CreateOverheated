package net.ironf.overheated.utility;

import com.mojang.serialization.MapCodec;
import net.ironf.overheated.steamworks.blocks.turbine.turbineFan.turbineFanBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class SmartDirectionalBlock extends DirectionalBlock {
    protected SmartDirectionalBlock(Properties p_52591_) {
        super(p_52591_);
    }

    //Codec
    public static final MapCodec<SmartDirectionalBlock> CODEC = simpleCodec(SmartDirectionalBlock::new);

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }


    //Block State
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite());
    }

}
