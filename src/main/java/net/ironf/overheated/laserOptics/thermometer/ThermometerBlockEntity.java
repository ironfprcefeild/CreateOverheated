package net.ironf.overheated.laserOptics.thermometer;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.SmartLaserMachineBlockEntity;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ThermometerBlockEntity extends SmartLaserMachineBlockEntity implements IHaveGoggleInformation {
    public ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        GoggleHelper.heatTooltip(tooltip,totalLaserHeat, HeatDisplayType.READING);

        float sunken = getCoolingUnits();
        if (sunken > 0) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.thermometer.total_sunken_heat").withStyle(ChatFormatting.WHITE)));
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(sunken)).withStyle(ChatFormatting.AQUA), 1));
        }
        return true;
    }

}
