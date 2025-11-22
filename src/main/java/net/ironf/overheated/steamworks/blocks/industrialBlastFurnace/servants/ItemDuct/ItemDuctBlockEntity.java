package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.ItemDuct;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.BlastFurnaceServantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemDuctBlockEntity extends BlastFurnaceServantBlockEntity {
    public ItemDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && controllerPos != null) {
            return ((BlastFurnaceControllerBlockEntity) level.getBlockEntity(controllerPos)).getInputLazyItemHandler().cast();
        }
        return super.getCapability(cap, side);
    }
}
