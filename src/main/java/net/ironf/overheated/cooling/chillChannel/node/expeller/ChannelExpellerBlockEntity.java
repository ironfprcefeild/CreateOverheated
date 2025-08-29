package net.ironf.overheated.cooling.chillChannel.node.expeller;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.cooling.chillChannel.network.ChannelSlotBox;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
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

public class ChannelExpellerBlockEntity extends SmartBlockEntity implements IChillChannelHook, ICoolingBlockEntity, IHaveGoggleInformation {

    public ChannelExpellerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    ScrollValueBehaviour expellScrollWheel;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        expellScrollWheel =
                new ScrollValueBehaviour(
                        Component.translatable("coverheated.chill_channel.expeller.scroll"),
                        this,
                        new ChannelSlotBox())
                        .between(0,256);

        behaviours.add(expellScrollWheel);
    }

    //Goggles

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


    //Cooling Stuff


    @Override
    public CoolingData getGeneratedCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in) {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (facing.getOpposite() == in
                && (level.getBlockState(cooledPos).getBlock() != AllBlocks.COOLER.get())
                && (level.getBlockState(cooledPos).getBlock() != AllBlocks.CHANNEL_ABSORBER.get())) {

            return lastCooler;
        } else {
            return CoolingData.empty();
        }
    }
    //Channel network stuff
    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- == 0){
            tickTimer = 20;
            if (!drawFromPosSet) {
                lastCapacity = 0;
                lastCoolingUnits = 0;
                lastMinTemp = 0;
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
                    //Subtract from channel based on how much the wheel is set too
                    int expelled = getExpelled();
                    lastCoolingUnits = hookEntity.getCoolingUnitsCapacity() - expelled;
                    if (lastCoolingUnits < 0){
                        //Too much drain, invalidate this node.
                        invalidateNode();
                    } else {
                        lastCooler = new CoolingData(expelled,lastMinTemp);
                    }
                }
                sendParticlesToo(drawsFromPos,this);
            } else {
                //No hook to pull from
                invalidateNode();
            }
        }
    }

    public int getExpelled(){
        return (level.getBlockState(getBlockPos().below()) == AllBlocks.CHILLSTEEL_COIL.getDefaultState()) ? (int) Math.pow(expellScrollWheel.getValue(), 2) : expellScrollWheel.getValue();
    }

    void invalidateNode(){
        //We are invalid
        lastEff = 0f;
        lastCapacity = 0;
        lastCoolingUnits = 0;
        lastMinTemp = 10000;
        lastCooler = CoolingData.empty();
    }

    BlockPos drawsFromPos;
    BlockPos sendToPos = null;
    int tickTimer = 1;
    int lastCapacity;
    float lastEff;
    float lastCoolingUnits;
    float lastMinTemp;
    boolean drawFromPosSet;
    boolean sendTooPosSet;
    CoolingData lastCooler = CoolingData.empty();

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        lastEff = tag.getFloat("eff");
        lastCapacity = tag.getInt("capacity");
        lastCoolingUnits = tag.getFloat("coolingunits");
        lastMinTemp = tag.getFloat("lastmintemp");
        lastCooler = CoolingData.readTag(tag,"lastcooler");
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
        if (lastCooler!=null) lastCooler.writeTag(tag,"lastcooler");
        tag.putBoolean("posset", drawFromPosSet);
        if (drawFromPosSet) tag.putLong("drawsfrom", drawsFromPos.asLong());
        if (sendToPos == null) {
            tag.putBoolean("sendtoset",false);
        } else {
            tag.putBoolean("sendtoset",true);
            tag.putLong("sendto",sendToPos.asLong());
        }
    }

    //Network Stuff
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
}
