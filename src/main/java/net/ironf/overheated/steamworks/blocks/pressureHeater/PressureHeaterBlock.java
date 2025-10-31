package net.ironf.overheated.steamworks.blocks.pressureHeater;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PressureHeaterBlock extends Block implements IBE<PressureHeaterBlockEntity> {
    public PressureHeaterBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<PressureHeaterBlockEntity> getBlockEntityClass() {
        return PressureHeaterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PressureHeaterBlockEntity> getBlockEntityType() {
        return AllBlockEntities.PRESSURE_HEATER.get();
    }

}
