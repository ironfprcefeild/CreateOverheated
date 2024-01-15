package net.ironf.overheated.laserOptics.backend.beam.laserCyst;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LaserCystBlock extends Block implements IBE<LaserCystBlockEntity> {
    public LaserCystBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<LaserCystBlockEntity> getBlockEntityClass() {
        return LaserCystBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LaserCystBlockEntity> getBlockEntityType() {
        return AllBlockEntities.LASER_CYST.get();
    }
}
