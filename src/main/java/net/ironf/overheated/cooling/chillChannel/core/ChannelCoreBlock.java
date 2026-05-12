package net.ironf.overheated.cooling.chillChannel.core;

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

public class ChannelCoreBlock extends SmartDirectionalBlock implements IBE<ChannelCoreBlockEntity> {
    public ChannelCoreBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<ChannelCoreBlockEntity> getBlockEntityClass() {
        return ChannelCoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChannelCoreBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHANNEL_CORE.get();
    }

}

