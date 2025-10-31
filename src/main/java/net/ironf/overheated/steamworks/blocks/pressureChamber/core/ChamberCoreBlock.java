package net.ironf.overheated.steamworks.blocks.pressureChamber.core;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ChamberCoreBlock extends Block implements IBE<ChamberCoreBlockEntity> {
    public ChamberCoreBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Class<ChamberCoreBlockEntity> getBlockEntityClass() {
        return ChamberCoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChamberCoreBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHAMBER_CORE.get();
    }




}
