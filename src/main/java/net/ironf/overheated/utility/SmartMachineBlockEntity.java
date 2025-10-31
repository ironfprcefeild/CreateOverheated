package net.ironf.overheated.utility;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;


public abstract class SmartMachineBlockEntity extends SmartBlockEntity {
    public SmartMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public int fiveTickTimer = 5;
    @Override
    public void tick() {
        super.tick();
        if (fiveTickTimer-- == 0){
            fiveTickTimer = 5;
            fifthTick();
        }
    }
    public void fifthTick(){
        if (doCooling()){
            coolingStep();
        }
    }

    //Cooling -----

    public float currentTemp = 10;
    public float coolingProgress = 0;
    public float getCurrentTemp(){
        return currentTemp;
    }
    public float addTemp(float toAdd){
        currentTemp += toAdd;
        return currentTemp;
    }
    public float getCoolingUnits(BlockPos pos, Level level){
        return getCoolingData(pos,level).coolingUnits;
    }
    public float getCoolingUnits(){
        return getCoolingData(getBlockPos(),level).coolingUnits;
    }

    public CoolingData getCoolingData(){
        return getCoolingData(getBlockPos(),level);
    }

    //Pos is the blockpos of the current block entity, which can be useful to pass in for some applications
    public CoolingData getCoolingData(BlockPos pos, Level level) {
        return getCoolingData(pos,level,Iterate.directions);
    }
    public CoolingData getCoolingData(BlockPos pos, Level level, Direction[] directions){
        CoolingData runningTotal = new CoolingData(0,10000);
        for (Direction d : directions){
            runningTotal.add(getCoolingDataFromDirection(pos,level,d));
        }
        if (hasPassiveCooling()){
            runningTotal.add(getPassiveCooling());
        }
        return runningTotal;
    }

    public CoolingData getCoolingDataFromDirection(BlockPos pos, Level level, Direction d){
        BlockPos check = pos.relative(d);
        BlockEntity be = level.getBlockEntity(check);
        if (be instanceof ICoolingBlockEntity){
            return (((ICoolingBlockEntity) be).getGeneratedCoolingData(check,pos,level,d));
        }
        return CoolingData.empty();
    }


    public boolean doCooling(){
        return hasPassiveCooling();
    }
    public boolean hasPassiveCooling(){
        return getPassiveCooling() != null;
    }
    public CoolingData getPassiveCooling(){
        return null;
    }
    public float getCooledDifficulty(){
        return (float) Math.pow(Math.abs(currentTemp),2);
    }
    public void coolingStep(){
        CoolingData cooling = getCoolingData(getBlockPos(),level);
        if (currentTemp > cooling.minTemp){
            coolingProgress += cooling.coolingUnits;
            while(coolingProgress >= getCooledDifficulty()) {
                coolingProgress = coolingProgress - getCooledDifficulty();
                currentTemp--;
            }
        }
    }

    public void tempAndCoolInfo(List<Component> tooltip){
        //Display: Current Temperature, Cooling Units, and Minimum Temperature
        CoolingData cooling = getCoolingData(getBlockPos(),level);

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.cooling.temperature").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(currentTemp)).withStyle(ChatFormatting.AQUA), 1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.cooling.cooling_units").withStyle(ChatFormatting.WHITE)));
        if (hasPassiveCooling()){
            CoolingData passiveCooling = getPassiveCooling();
            tooltip.add(GoggleHelper.addIndent(
                    Component.translatable("coverheated.tooltip.cooling.outside")
                    .append(Component.literal(GoggleHelper.easyFloat(cooling.coolingUnits-passiveCooling.coolingUnits)))
                    .withStyle(ChatFormatting.AQUA),1));
            tooltip.add(GoggleHelper.addIndent(
                    Component.translatable("coverheated.tooltip.cooling.passive")
                    .append(Component.literal(GoggleHelper.easyFloat(passiveCooling.coolingUnits)))
                    .withStyle(ChatFormatting.AQUA),1));

        } else {
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(cooling.coolingUnits)).withStyle(ChatFormatting.AQUA), 1));
        }
        if (cooling.minTemp != 10000) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.cooling.min_temp").withStyle(ChatFormatting.WHITE)));
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(cooling.minTemp)).withStyle(ChatFormatting.AQUA), 1));
        }
    }

    //Read/Writes -----

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        currentTemp = tag.getFloat("current_temp");
        coolingProgress = tag.getFloat("cooling_progress");
        fiveTickTimer = tag.getInt("five_tick_timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("current_temp",currentTemp);
        tag.putFloat("cooling_progress",coolingProgress);
        tag.putInt("five_tick_timer",fiveTickTimer);
    }

    //Goggles ------

    // Noises! ------
    public void makeSound(SoundEvent sound, float volume, float pitch){
        makeSound(sound,getBlockPos(),volume,pitch);
    }

    public void makeSound(AllSoundEvents.SoundEntry sound, float volume, float pitch){
        makeSound(sound.getMainEvent(),getBlockPos(),volume,pitch);
    }

    public void makeSound(SoundEvent sound, BlockPos pos, float volume, float pitch){
        Holder<SoundEvent> sfx = Holder.direct(sound);
        level.playSeededSound(null, pos.getX(), pos.getY(), pos.getZ(), sfx, SoundSource.BLOCKS,volume,pitch,0);
    }
}
