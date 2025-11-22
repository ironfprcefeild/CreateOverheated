package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.fluidDuct;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FluidDuctBlock extends Block implements IBE<FluidDuctBlockEntity> {
    public FluidDuctBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Class<FluidDuctBlockEntity> getBlockEntityClass() {
        return FluidDuctBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidDuctBlockEntity> getBlockEntityType() {
        return AllBlockEntities.FLUID_DUCT.get();
    }




}
