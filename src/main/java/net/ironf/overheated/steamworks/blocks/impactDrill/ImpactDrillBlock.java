package net.ironf.overheated.steamworks.blocks.impactDrill;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ImpactDrillBlock extends Block implements IBE<ImpactDrillBlockEntity> {
    public ImpactDrillBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<ImpactDrillBlockEntity> getBlockEntityClass() {
        return ImpactDrillBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ImpactDrillBlockEntity> getBlockEntityType() {
        return AllBlockEntities.IMPACT_DRILL.get();
    }
}
