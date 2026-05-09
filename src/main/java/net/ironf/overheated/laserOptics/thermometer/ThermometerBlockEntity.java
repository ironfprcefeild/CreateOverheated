package net.ironf.overheated.laserOptics.thermometer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.metalWorking.HeatedBlock;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.SmartLaserMachineBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

        for(Direction d : Iterate.horizontalDirections){
            if (level.getBlockState(getBlockPos().relative(d)).hasProperty(HeatedBlock.TEMPERATURE)){
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.thermometer.heated_block.direction."+d.getName())));
                BlockState heatedBlock = level.getBlockState(getBlockPos().relative(d));
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.thermometer.heated_block.temperature")
                        .append(String.valueOf(heatedBlock.getValue(HeatedBlock.TEMPERATURE).intValue()))
                        .withStyle(ChatFormatting.RED),1));
                tooltip.add(GoggleHelper.addIndent(
                        switch (heatedBlock.getValue(HeatedBlock.HEATLEVEL)){
                            case 2 -> Component.translatable("coverheated.tooltip.superheat").withStyle(ChatFormatting.BLUE);
                            case 3 -> Component.translatable("coverheated.tooltip.overheat").withStyle(ChatFormatting.LIGHT_PURPLE);
                            default -> Component.translatable("coverheated.tooltip.heat").withStyle(ChatFormatting.RED);
                        }
                        ,1));
            }
        }
        return true;
    }

}
