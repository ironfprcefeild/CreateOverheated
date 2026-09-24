package net.ironf.overheated.cooling.chillChannel;

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

public class ChannelBlock extends SmartDirectionalBlock implements IBE<ChannelBlockEntity> {
    public ChannelBlock(Properties p) {
        super(p);
    }

    /// BE

    @Override
    public Class<ChannelBlockEntity> getBlockEntityClass() {
        return ChannelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChannelBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHANNEL.get();
    }
}
