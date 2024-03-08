package net.ironf.overheated.steamworks.blocks.pressureChamber.core;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChamberCoreBlock extends Block implements IBE<ChamberCoreBlockEntity> {
    public ChamberCoreBlock(Properties properties) {
        super(properties);
    }

    public static void updateChamberState(BlockState pState, Level pLevel, BlockPos relative) {
        ((ChamberCoreBlockEntity) pLevel.getBlockEntity(relative)).updateAdditions(true);
    }

    @Override
    public Class<ChamberCoreBlockEntity> getBlockEntityClass() {
        return ChamberCoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChamberCoreBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHAMBER_CORE.get();
    }

    public static boolean isCore(BlockState state) {
        return state.getBlock() instanceof ChamberCoreBlock;
    }



}
