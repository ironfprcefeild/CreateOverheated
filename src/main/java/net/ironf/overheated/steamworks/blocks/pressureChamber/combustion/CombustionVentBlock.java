package net.ironf.overheated.steamworks.blocks.pressureChamber.combustion;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CombustionVentBlock extends Block implements IBE<CombustionVentBlockEntity> {
    public CombustionVentBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<CombustionVentBlockEntity> getBlockEntityClass() {
        return CombustionVentBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CombustionVentBlockEntity> getBlockEntityType() {
        return AllBlockEntities.COMBUSTION_VENT.get();
    }
}
