package net.ironf.overheated.cooling.chillChannel.core;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ChannelCoreBlock extends Block implements IBE<ChannelCoreBlockEntity> {
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
