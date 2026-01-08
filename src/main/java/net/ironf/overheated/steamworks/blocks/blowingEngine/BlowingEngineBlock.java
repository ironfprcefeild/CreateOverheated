package net.ironf.overheated.steamworks.blocks.blowingEngine;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlowingEngineBlock extends Block implements IBE<BlowingEngineBlockEntity> {
    public BlowingEngineBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<BlowingEngineBlockEntity> getBlockEntityClass() {
        return BlowingEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlowingEngineBlockEntity> getBlockEntityType() {
        return AllBlockEntities.BLOWING_ENGINE.get();
    }
}
