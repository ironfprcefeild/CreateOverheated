package net.ironf.overheated.laserOptics.solarPanel;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.List;

public class SolarPanelBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public SolarPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        
    }

    public int processingTicks = 5;
    public float recentReading = 0;
    
    @Override
    public void tick() {
        super.tick();
        if (processingTicks-- < 1){
            processingTicks = 300;
            updateHeat();
        }
    }

    public void updateHeat() {
        //Returns 0 if it cant see the sky
        //0.5 in hot biome day, 0.25 in hot biome night.
        //0.25 in normal day, 0.125 in normal night.
        if (level.canSeeSky(getBlockPos().above())){
            recentReading = (level.getBiome(getBlockPos()).is(Tags.Biomes.IS_HOT)) ? 0.5f : 0.25f;
            recentReading = (level.getDayTime() < 13000) ? recentReading : recentReading/2;
        } else {
            recentReading = 0;
        }

    }

    public HeatData getRecentReading(){
        return new HeatData(recentReading,0,0);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        processingTicks = tag.getInt("processing_ticks");
        recentReading =  tag.getFloat("recent_read");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("processing_ticks",processingTicks);
        tag.putFloat("recent_read",recentReading);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        GoggleHelper.heatTooltip(tooltip,getRecentReading(), HeatDisplayType.SUPPLYING,3);
        if (isPlayerSneaking) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.solar_panel.updating").append(String.valueOf(processingTicks)).append(Component.translatable("coverheated.solar_panel.ticks"))));
        }
        return true;
    }
}
