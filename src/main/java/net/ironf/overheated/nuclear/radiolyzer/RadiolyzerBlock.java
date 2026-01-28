package net.ironf.overheated.nuclear.radiolyzer;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RadiolyzerBlock extends Block implements IBE<RadiolyzerBlockEntity> {
    public RadiolyzerBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<RadiolyzerBlockEntity> getBlockEntityClass() {
        return RadiolyzerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RadiolyzerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.RADIOLYZER.get();
    }
}
