package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class ChamberAdditionBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public ChamberAdditionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    BlockPos corePos;

    @Override
    public void initialize() {
        super.initialize();
        BlockPos pos = getBlockPos();
        corePos = pos.relative(ChamberAdditionBlock.getAttachedDirection(level.getBlockState(pos)).getOpposite());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.pressure_chamber.crouch_for_chamber_info")));
        if (isPlayerSneaking) {
            ((ChamberCoreBlockEntity) level.getBlockEntity(corePos)).pullTooltip();
        } else {
            otherGoggleInfo(tooltip, false);
        }

        return true;


    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putLong("corepos",corePos.asLong());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        corePos = BlockPos.of(tag.getLong("corepos"));
    }

    public void otherGoggleInfo(List<Component> tooltip, boolean isPlayerSneaking) {
    }
}
