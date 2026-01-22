package net.ironf.overheated.laserOptics.DiodeJunction;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.SmartLaserMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DiodeJunctionBlockEntity extends SmartLaserMachineBlockEntity implements IHaveGoggleInformation {
    public DiodeJunctionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    ///Scroll Wheel
    public ScrollValueBehaviour transferScrollWheel;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        transferScrollWheel =
                new ScrollValueBehaviour(Component.translatable("coverheated.diode.junction.scroll"), this, new JunctionSlotBox())
                        .between(0,16);
        behaviours.add(transferScrollWheel);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        GoggleHelper.heatTooltip(tooltip,totalLaserHeat, HeatDisplayType.TRANSFERRING);
        return true;
    }

    @Override
    public boolean doCooling() {
        return false;
    }


}
