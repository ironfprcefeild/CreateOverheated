package net.ironf.overheated.cooling.chillChannel.node.absorber;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ChannelAbsorberBlockEntity extends SmartMachineBlockEntity implements IChillChannelHook, IHaveGoggleInformation {
    public ChannelAbsorberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //This block has no temperature of its own
    @Override
    public boolean doCooling() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- == 0){
            tickTimer = 20;
            if (!drawFromPosSet) {
                lastCapacity = 0;
                lastCoolingUnits = 0;
                lastMinTemp = 10000;
                lastEff = 0;
                return;
            }
            BlockEntity drawsFromEntity = level.getBlockEntity(drawsFromPos);
            if (drawsFromEntity instanceof IChillChannelHook hookEntity){
                //This channels draw point is still valid
                lastCapacity = hookEntity.getBlockCapacity() - 1;
                lastEff = hookEntity.getNetworkEfficency();
                lastMinTemp = hookEntity.getNetworkMinTemp();
                if (lastCapacity < 0 || lastEff == 0){
                    //Channel is invalid, marked by an efficiency of 0
                    invalidateNode();
                } else {
                    //Add the cooling units on the channel with the ones that this block is receiving.
                    lastCoolingUnits = hookEntity.getCoolingUnitsCapacity() + getCoolingUnits() * lastEff;
                }
                sendParticlesToo(drawsFromPos,this);
            } else {
                //No hook to pull from
                invalidateNode();
            }
        }
    }

    void invalidateNode(){
        //We are invalid
        lastEff = 0f;
        lastCapacity = 0;
        lastCoolingUnits = 0;
        lastMinTemp = 10000;
    }

    BlockPos drawsFromPos;
    BlockPos sendToPos = null;
    int tickTimer = 1;
    int lastCapacity;
    float lastEff;
    float lastMinTemp;
    float lastCoolingUnits;
    boolean drawFromPosSet = false;
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        lastEff = tag.getFloat("eff");
        lastCapacity = tag.getInt("capacity");
        lastCoolingUnits = tag.getFloat("coolingunits");
        lastMinTemp = tag.getFloat("lastmintemp");
        drawFromPosSet = tag.getBoolean("posset");
        drawsFromPos = drawFromPosSet ? BlockPos.of(tag.getLong("drawsfrom")) : null;
        sendToPos = tag.getBoolean("sendtoset") ? BlockPos.of(tag.getLong("sendto")) : null;

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag,clientPacket);
        tag.putInt("ticktimer",tickTimer);
        tag.putFloat("eff",lastEff);
        tag.putInt("capacity",lastCapacity);
        tag.putFloat("coolingunits",lastCoolingUnits);
        tag.putFloat("lastmintemp",lastMinTemp);
        tag.putBoolean("posset", drawFromPosSet);
        if (drawFromPosSet) {
            tag.putLong("drawsfrom", drawsFromPos.asLong());
        }
        if (sendToPos == null) {
            tag.putBoolean("sendtoset",false);
        } else {
            tag.putBoolean("sendtoset",true);
            tag.putLong("sendto",sendToPos.asLong());
        }
    }

    @Override
    public int getBlockCapacity() {
        return lastCapacity;
    }

    @Override
    public float getNetworkEfficency() {
        return lastEff;
    }

    @Override
    public float getNetworkMinTemp() {
        return lastMinTemp;
    }

    public float getCoolingUnitsCapacity() {
        return lastCoolingUnits;
    }


    @Override
    public void setDrawFrom(BlockPos bp) {
        drawsFromPos = bp;
        drawFromPosSet = true;
    }

    @Override
    public void unsetDrawFrom() {
        drawsFromPos = null;
        drawFromPosSet = false;
    }

    @Override
    public void setSendToo(BlockPos bp) {
        if (sendToPos != null) {
            BlockEntity sbe = level.getBlockEntity(sendToPos);
            if (sbe instanceof IChillChannelHook oldSendingToo) {
                oldSendingToo.unsetDrawFrom();
            }
        }
        sendToPos = bp;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.node." + (drawFromPosSet ? "draws_from" : "unset"))));
        if (drawFromPosSet){
            tooltip.add(GoggleHelper.addIndent(Component.literal(drawsFromPos.toString().replace("BlockPos","")),1));
        }
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.cooling_units").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(lastCoolingUnits)).withStyle(ChatFormatting.AQUA),1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.mintemp").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(lastMinTemp)).withStyle(ChatFormatting.AQUA),1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.eff").append(String.valueOf(lastEff))));
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.capacity").append(String.valueOf(lastCapacity))));
        return true;
    }
}
