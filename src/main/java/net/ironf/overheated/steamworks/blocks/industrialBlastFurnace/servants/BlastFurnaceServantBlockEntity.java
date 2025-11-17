package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlastFurnaceServantBlockEntity extends SmartBlockEntity {
    public BlastFurnaceServantBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockPos controllerPos;
    public void updateController(BlockPos bp){
        this.controllerPos = bp;

    }

    public void decoupleController(){
        updateController(null);
        BlockEntity BE = level.getBlockEntity(controllerPos);
        if (BE instanceof BlastFurnaceControllerBlockEntity controllerBE){
            controllerBE.removeServant(getBlockPos());
        }
    }
}
