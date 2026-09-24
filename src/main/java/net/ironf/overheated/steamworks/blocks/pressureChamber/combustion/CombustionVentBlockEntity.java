package net.ironf.overheated.steamworks.blocks.pressureChamber.combustion;

import net.ironf.overheated.utility.machines.CapableMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CombustionVentBlockEntity extends CapableMachineBlockEntity {
    public CombustionVentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getFluidCapacity() {
        return 2000;
    }
}
