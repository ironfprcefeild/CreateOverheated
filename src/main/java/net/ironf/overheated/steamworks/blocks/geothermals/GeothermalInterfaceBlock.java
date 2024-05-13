package net.ironf.overheated.steamworks.blocks.geothermals;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GeothermalInterfaceBlock extends Block implements IBE<GeothermalInterfaceBlockEntity> {
    public GeothermalInterfaceBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<GeothermalInterfaceBlockEntity> getBlockEntityClass() {
        return GeothermalInterfaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GeothermalInterfaceBlockEntity> getBlockEntityType() {
        return AllBlockEntities.GEOTHERMAL_INTERFACE.get();
    }
}
