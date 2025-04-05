package net.ironf.overheated.cooling.chillChannel.node.absorber;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ChannelAbsorberBlock extends Block implements IBE<ChannelAbsorberBlockEntity> {
    public ChannelAbsorberBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<ChannelAbsorberBlockEntity> getBlockEntityClass() {
        return ChannelAbsorberBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChannelAbsorberBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHANNEL_ABSORBER.get();
    }
}
