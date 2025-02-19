package net.ironf.overheated.laserOptics.solarPanel;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBiomeTagsProvider;
import org.checkerframework.checker.units.qual.C;

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
        //Returns 0 if it cant see the sky, if it can the running total starts at 0.25
        //Gains 0.25 more if during the day
        //Gains 0.25 more if in hot biomes
        if (level.canSeeSky(getBlockPos().above())){
            if (level.getBiome(getBlockPos()).is(Tags.Biomes.IS_HOT)){
                recentReading = 0.5f;
            } else {
                recentReading = 0.25f;
            }
            if (level.getDayTime() < 13000) {
                recentReading = recentReading + 0.25f;
            }
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
        GoggleHelper.heatTooltip(tooltip,getRecentReading(), HeatDisplayType.SUPPLYING);
        if (isPlayerSneaking) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.solar_panel.updating").append(String.valueOf(processingTicks)).append(Component.translatable("coverheated.solar_panel.ticks"))));
        }
        return true;
    }
}
