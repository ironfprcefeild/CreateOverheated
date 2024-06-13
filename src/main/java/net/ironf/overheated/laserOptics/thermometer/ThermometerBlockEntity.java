package net.ironf.overheated.laserOptics.thermometer;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.blocks.heatsink.HeatSinkHelper;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ThermometerBlockEntity extends SmartBlockEntity implements ILaserAbsorber, IHaveGoggleInformation, HeatSinkHelper {
    public ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    HeatData lastRead = HeatData.empty();
    int timer = 0;
    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat, int d) {
        lastRead = beamHeat.copyMe();
        timer = 20;
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (timer > 0){
            timer--;
        } else {
            lastRead = HeatData.empty();
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        GoggleHelper.heatTooltip(tooltip,lastRead, HeatDisplayType.READING);

        float sunken = getHeatSunkenFrom(getBlockPos(),level);
        if (sunken > 0) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.thermometer.total_sunken_heat").withStyle(ChatFormatting.WHITE)));
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(sunken)).withStyle(ChatFormatting.AQUA), 1));
        }
        return true;
    }
}
