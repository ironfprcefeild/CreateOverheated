package net.ironf.overheated.laserOptics.thermometer;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

public class ThermometerBlockEntity extends SmartBlockEntity implements ILaserAbsorber, IHaveGoggleInformation {
    public ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    HeatData lastRead = HeatData.empty();
    int timer = 0;
    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat) {
        lastRead = beamHeat;
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
        if (lastRead != HeatData.empty()){
            tooltip.add(Component.literal(" "));
            tooltip.add(Component.translatable("coverheated.heat").append(String.valueOf(lastRead.Heat)));
            tooltip.add(Component.translatable("coverheated.superheat").append(String.valueOf(lastRead.SuperHeat)));
            tooltip.add(Component.translatable("coverheated.overheat").append(String.valueOf(lastRead.OverHeat)));
        } else {
            tooltip.add(Component.translatable("coverheated.no_heat"));
        }
        return true;
    }
}
