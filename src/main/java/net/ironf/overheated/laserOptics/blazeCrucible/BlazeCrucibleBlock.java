package net.ironf.overheated.laserOptics.blazeCrucible;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlazeCrucibleBlock extends Block implements IBE<BlazeCrucibleBlockEntity> {
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
