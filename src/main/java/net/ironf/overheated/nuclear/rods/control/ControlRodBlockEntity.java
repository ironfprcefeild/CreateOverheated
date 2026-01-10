package net.ironf.overheated.nuclear.rods.control;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.ironf.overheated.cooling.chillChannel.expeller.ChannelSlotBox;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ControlRodBlockEntity extends SmartBlockEntity implements ControlRodsRegister.IControlRod {
    public ControlRodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    ScrollValueBehaviour gateScrollWheel;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        gateScrollWheel =
                new ScrollValueBehaviour(Component.translatable("coverheated.control_rod.scroll"), this, new ChannelSlotBox())
                        .between(0,16)
                        .withCallback( i -> {propagateScrollValue(i,Direction.UP);
                                                    propagateScrollValue(i,Direction.DOWN);});

        behaviours.add(gateScrollWheel);
    }

    public void setGateScrollNoCallBack(int newValue){
        newValue = Mth.clamp(newValue, 0, 16);
        if (newValue == gateScrollWheel.value)
            return;
        gateScrollWheel.value = newValue;
        this.setChanged();
        this.sendData();
    }

    public void propagateScrollValue(int newValue, Direction d){
        if (level.getBlockEntity(getBlockPos().relative(d)) instanceof ControlRodBlockEntity CRBE){
            //Update value
            CRBE.setGateScrollNoCallBack(newValue);
            CRBE.propagateScrollValue(newValue,d);
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (level.getBlockEntity(getBlockPos().relative(Direction.UP)) instanceof ControlRodBlockEntity CRBE){
            CRBE.propagateScrollValue(CRBE.gateScrollWheel.getValue(),Direction.UP);
            CRBE.propagateScrollValue(CRBE.gateScrollWheel.getValue(),Direction.DOWN);
        } else if (level.getBlockEntity(getBlockPos().relative(Direction.DOWN)) instanceof ControlRodBlockEntity CRBE){
            CRBE.propagateScrollValue(CRBE.gateScrollWheel.getValue(),Direction.DOWN);
            this.gateScrollWheel.setValue(CRBE.gateScrollWheel.getValue());
        }
    }

    @Override
    public Integer regulate(int incomingNeutrinos, Direction direction, BlockPos pos, BlockState state, Level level) {
        return Math.min(gateScrollWheel.getValue(),incomingNeutrinos);
    }
}
