package net.ironf.overheated.nuclear.rods.fuel;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FuelRodBlock extends Block implements IBE<FuelRodBlockEntity> {
    public FuelRodBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<FuelRodBlockEntity> getBlockEntityClass() {
        return FuelRodBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FuelRodBlockEntity> getBlockEntityType() {
        return AllBlockEntities.FUEL_ROD.get();
    }


}
