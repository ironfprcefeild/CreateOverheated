package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.item;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionType;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.IChamberAdditionBlockEntity;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.ref.WeakReference;
import java.util.List;

public class ChamberItemBlockEntity extends ChamberAdditionBlockEntity implements IChamberAdditionBlockEntity {
    public ChamberItemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public ChamberAdditionType getAdditionType() {
        return ChamberAdditionType.ITEM;
    }

    public WeakReference<ItemVaultBlockEntity> source;
    public ItemVaultBlockEntity getVault() {
        ItemVaultBlockEntity vault = source.get();
        if (vault == null || vault.isRemoved()) {
            if (vault != null)
                source = new WeakReference<>(null);
            Direction facing = ChamberAdditionBlock.getAttachedDirection(getBlockState());
            BlockEntity be = level.getBlockEntity(worldPosition.relative(facing.getOpposite()));
            if (be instanceof ItemVaultBlockEntity tankBe)
                source = new WeakReference<>(vault = tankBe);
        }
        if (vault == null)
            return null;
        return vault.getControllerBE();
    }
}
