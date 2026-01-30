package net.ironf.overheated.cooling.chillChannel.expeller;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.cooling.chillChannel.ChannelBlockEntity;
import net.ironf.overheated.cooling.chillChannel.MutableDirection;
import net.ironf.overheated.cooling.chillChannel.core.ChannelStatusBundle;
import net.ironf.overheated.cooling.cooler.CoolerBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.Objects;

public class ChannelExpellerBlockEntity extends ChannelBlockEntity implements ICoolingBlockEntity, IHaveGoggleInformation {
    public ChannelExpellerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    int timer = 10;
    float lastEff = 0;
    CoolingData output = CoolingData.empty();
    ChannelStatusBundle lastStatus = new ChannelStatusBundle();
    boolean loopComplete = false;
    String errorMessage = "not.on.network";
    @Override
    public void tick() {
        if (timer-- == 0){
            timer = 210;
            lastEff = 0;
            lastStatus = new ChannelStatusBundle();
            output = CoolingData.empty();
            errorMessage = "not.on.network";
            loopComplete = false;
        }
    }

    @Override
    public BlockPos propagateChannel(ChannelStatusBundle status, float efficiency, float minTemp, MutableDirection channelMovingIn) {
        //Calculate Expelled
        int expelled = expelScrollWheel.getValue();
        if (level.getBlockState(getBlockPos().relative(
                level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING).getOpposite()))
                .is(AllBlocks.CHILLSTEEL_COIL.get())){
            expelled *= expelled;
        }

        //Update Cooling data if valid
        if (status.getDelta() >= expelled){
            status.addLoad(expelled);
            output = new CoolingData(expelled,minTemp);
            errorMessage = "";
        } else {
            output = CoolingData.empty();
            errorMessage = "not.enough.cooling";
        }

        //Set timer
        timer = 250;
        lastEff = efficiency;
        lastStatus = status;

        //Loop track
        loopComplete = false;

        //Return
        //  we don't change ChannelMovingIn so that it continues in the same direction
        //  normal channels will change this to the direction they face
        return getBlockPos().relative(channelMovingIn.getImmutable());
    }

    @Override
    public void acceptNetwork() {
        loopComplete = true;
    }

    ///Cooling (other blocks)
    @Override
    public CoolingData getGeneratedCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in) {
        //Checks to ensure that the cooler is facing into the cooled block, we have coolant,and not cooling a cooler
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (!loopComplete || facing.getOpposite() != in){
            return CoolingData.empty();
        }
        if (level.getBlockState(cooledPos).getBlock() == AllBlocks.CHANNEL.get()) {
            ((ChannelBlockEntity) Objects.requireNonNull(level.getBlockEntity(cooledPos))).applyEfficiency = false;
            return output;
        } else if (level.getBlockState(cooledPos).getBlock() != AllBlocks.COOLER.get()) {
            return output;
        } else {
            return ((CoolerBlockEntity) level.getBlockEntity(cooledPos)).effTracker == 1 ? output : CoolingData.empty();
        }
    }

    ///Scroll Wheel
    ScrollValueBehaviour expelScrollWheel;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        expelScrollWheel =
                new ScrollValueBehaviour(Component.translatable("coverheated.channel.expeller.scroll"), this, new ChannelSlotBox())
                        .between(0,256);
        behaviours.add(expelScrollWheel);
    }
    /// Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (errorMessage != "") {
            tooltip.add(GoggleHelper.addIndent(
                    Component.translatable("coverheated.chill_channel.expeller.error." + errorMessage)));
        }
        if (!loopComplete){
            tooltip.add(GoggleHelper.addIndent(
                    Component.translatable("coverheated.chill_channel.expeller.incomplete_loop")));
        }

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.network_status").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(lastStatus.usedCooling) + "/" + GoggleHelper.easyFloat(lastStatus.maximumCooling)).withStyle(lastStatus.getDelta() >= 0 ? ChatFormatting.AQUA : ChatFormatting.RED),1));


        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.expelling_cooling_units").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(loopComplete ? output.coolingUnits : 0)).withStyle(ChatFormatting.AQUA),1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.expelling_mintemp").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(output.minTemp)).withStyle(ChatFormatting.AQUA),1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.chill_channel.eff").append(String.valueOf(lastEff))));
        return true;
    }
    ///Read/Write
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
        lastEff = tag.getFloat("lasteff");
        errorMessage = tag.getString("error");
        output = CoolingData.readTag(tag,"output");
        lastStatus = new ChannelStatusBundle(tag,"laststatus");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        tag.putFloat("lasteff",lastEff);
        tag.putString("error",errorMessage);
        output.writeTag(tag,"output");
        lastStatus.write(tag,"laststatus");
    }


}
