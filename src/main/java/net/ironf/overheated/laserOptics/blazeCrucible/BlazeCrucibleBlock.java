package net.ironf.overheated.laserOptics.blazeCrucible;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlazeCrucibleBlock extends Block implements IBE<BlazeCrucibleBlockEntity> {
    //Alot of this code is ripped straight from the blaze burner because of how the heat level system is setup (poorly to be honest)

    public static final EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = EnumProperty.create("blaze", BlazeBurnerBlock.HeatLevel.class);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT_LEVEL);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (world.isClientSide)
            return;
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        if (!(blockEntity instanceof BasinBlockEntity))
            return;
        BasinBlockEntity basin = (BasinBlockEntity) blockEntity;
        basin.notifyChangeOfContents();
    }

    public static int getLight(BlockState state) {
        BlazeBurnerBlock.HeatLevel level = state.getValue(HEAT_LEVEL);
        return switch (level) {
            case NONE -> 0;
            case SMOULDERING,KINDLED -> 8;
            case SEETHING -> 15;
            default -> 20;
        };
    }

    public BlazeCrucibleBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<BlazeCrucibleBlockEntity> getBlockEntityClass() {
        return BlazeCrucibleBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlazeCrucibleBlockEntity> getBlockEntityType() {
        return AllBlockEntities.BLAZE_CRUCIBLE.get();
    }

}
