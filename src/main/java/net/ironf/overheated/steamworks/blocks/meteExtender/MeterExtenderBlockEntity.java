package net.ironf.overheated.steamworks.blocks.meteExtender;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class MeterExtenderBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public MeterExtenderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if ((level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING)))) instanceof IHaveGoggleInformation target){
            return target.addToGoggleTooltip(tooltip,isPlayerSneaking);
        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.meter_extender.no_read")));
            return false;
        }
    }
}
