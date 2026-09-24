package net.ironf.overheated.steamworks.blocks.meterExtender;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.utility.SmartDirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class MeterExtenderBlock extends SmartDirectionalBlock implements IBE<MeterExtenderBlockEntity> {
    public MeterExtenderBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<MeterExtenderBlockEntity> getBlockEntityClass() {
        return MeterExtenderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MeterExtenderBlockEntity> getBlockEntityType() {
        return AllBlockEntities.METER_EXTENDER.get();
    }

}
