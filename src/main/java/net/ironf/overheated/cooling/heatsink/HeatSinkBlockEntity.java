package net.ironf.overheated.cooling.heatsink;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.cooling.IAirCurrentReader;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class HeatSinkBlockEntity extends SmartBlockEntity implements IAirCurrentReader, ICoolingBlockEntity, IHaveGoggleInformation {
    public HeatSinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    ////Doing Stuff
    int timer = 5;
    float sunken = 0;

    @Override
    public void tick() {
        super.tick();
        if (timer <= 0){
            sunken = 0;
        } else {
            timer--;
        }
        
    }



    @Override
    public float getGeneratedCoolingUnits(BlockPos myPos, BlockPos cooledPos, Level level, Direction in) {
        return (getBlockState().getValue(BlockStateProperties.FACING).getAxis() == in.getAxis()) ? findTotalSunk(in) : 0f;
    }

    //This method is used by  to collect the total heat sink of a series of heat sinks

    public float findTotalSunk(Direction in){
        float runningTotal = findSunken();
        int i = 1;
        while (i != 5){
            BlockEntity be = level.getBlockEntity(getBlockPos().relative(in,i));
            if (be != null && be.getType() == AllBlockEntities.HEAT_SINK.get()){
                runningTotal = runningTotal + ((HeatSinkBlockEntity) be).findSunken();
            } else {
                return runningTotal;
            }
            i++;
        }
        return runningTotal;
    }

    public float findSunken(){
        return Math.abs(sunken);
    }

    @Override
    public void update(float strength, Direction incoming) {
        if (incoming.getAxis() == getBlockState().getValue(BlockStateProperties.FACING).getAxis()){
            sunken = 0;
            timer = 0;
            return;
        }
        sunken = strength/256;
        timer = 5;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.heat_sink.airflow").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(findSunken())).withStyle(ChatFormatting.AQUA),1));
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        sunken = tag.getFloat("sunken");
        timer = tag.getInt("timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("sunken",sunken);
        tag.putInt("timer",timer);
    }
}
