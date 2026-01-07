package net.ironf.overheated.nuclear.rods.control;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ControlRodBlock extends Block implements IBE<ControlRodBlockEntity> {
    public ControlRodBlock(Properties p) {
        super(p);
    }


    @Override
    public Class<ControlRodBlockEntity> getBlockEntityClass() {
        return ControlRodBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ControlRodBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CONTROL_ROD.get();
    }
}
