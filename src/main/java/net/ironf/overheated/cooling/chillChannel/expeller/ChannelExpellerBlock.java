package net.ironf.overheated.cooling.chillChannel.expeller;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ChannelExpellerBlock extends Block implements IBE<ChannelExpellerBlockEntity> {
    public ChannelExpellerBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<ChannelExpellerBlockEntity> getBlockEntityClass() {
        return ChannelExpellerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChannelExpellerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHANNEL_EXPELLER.get();
    }
}
