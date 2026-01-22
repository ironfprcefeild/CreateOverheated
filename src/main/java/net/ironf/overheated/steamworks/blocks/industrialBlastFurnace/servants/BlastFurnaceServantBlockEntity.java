package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public abstract class BlastFurnaceServantBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public BlastFurnaceServantBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockPos controllerPos;
    public void updateController(BlockPos bp){
        ///Overheated.LOGGER.info("servant" + (bp == null ? "taken" : "given") + "controller");
        this.controllerPos = bp;

    }

    public void decoupleController(){
        if (controllerPos != null){
            BlockEntity BE = level.getBlockEntity(controllerPos);
            updateController(null);
            if (BE instanceof BlastFurnaceControllerBlockEntity controllerBE){
                controllerBE.removeServant(getBlockPos());
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
        decoupleController();
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("controllerpos")) {
            controllerPos = BlockPos.of(tag.getLong("controllerpos"));
        } else {
            controllerPos = null;
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (controllerPos != null) {
            tag.putLong("controllerpos", controllerPos.asLong());
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (isPlayerSneaking) {
            if (controllerPos != null) {
                tooltip.add(addIndent(Component.translatable("coverheated.ibf.servant.controllerpos")));
                tooltip.add(addIndent(Component.literal(controllerPos.toString())));
            } else {
                tooltip.add(addIndent(Component.translatable("coverheated.ibf.servant.no_controller")));
            }
        }

        return true;
    }
}

