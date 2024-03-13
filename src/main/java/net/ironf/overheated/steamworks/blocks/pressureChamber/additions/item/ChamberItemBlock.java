package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.item;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ChamberItemBlock extends ChamberAdditionBlock implements IBE<ChamberItemBlockEntity> {
    public ChamberItemBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<ChamberItemBlockEntity> getBlockEntityClass() {
        return ChamberItemBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChamberItemBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHAMBER_ITEM.get();
    }
}
