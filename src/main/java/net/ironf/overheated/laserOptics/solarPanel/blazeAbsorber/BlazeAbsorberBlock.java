package net.ironf.overheated.laserOptics.solarPanel.blazeAbsorber;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlazeAbsorberBlock extends Block implements IBE<BlazeAbsorberBlockEntity> {
    public BlazeAbsorberBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<BlazeAbsorberBlockEntity> getBlockEntityClass() {
        return BlazeAbsorberBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlazeAbsorberBlockEntity> getBlockEntityType() {
        return AllBlockEntities.BLAZE_ABSORBER.get();
    }

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
}
