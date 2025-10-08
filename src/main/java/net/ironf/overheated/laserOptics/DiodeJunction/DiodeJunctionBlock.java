package net.ironf.overheated.laserOptics.DiodeJunction;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DiodeJunctionBlock extends Block implements IBE<DiodeJunctionBlockEntity> {
    public DiodeJunctionBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<DiodeJunctionBlockEntity> getBlockEntityClass() {
        return DiodeJunctionBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DiodeJunctionBlockEntity> getBlockEntityType() {
        return AllBlockEntities.DIODE_JUNCTION.get();
    }
}
