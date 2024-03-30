package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DiodeHeaters {
    //This is an adapted style of creates Boiler Heater Code
    private static final AttachedRegistry<Block, Heater> BLOCK_HEATERS = new AttachedRegistry<>(ForgeRegistries.BLOCKS);

    public static void registerHeater(ResourceLocation block, Heater heater) {
        BLOCK_HEATERS.register(block, heater);
    }

    public static void registerHeater(Block block, Heater heater) {
        BLOCK_HEATERS.register(block, heater);
    }


    public static HeatData getActiveHeat(Level level, BlockPos pos, BlockState state) {
        Heater heater = BLOCK_HEATERS.get(state.getBlock());
        if (heater != null) {
            return heater.getActiveHeat(level, pos, state);
        }

        return HeatData.empty();
    }

    public static HeatData getActiveHeat(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return getActiveHeat(level, pos, state);
    }

    public static void registerDefaults() {
        Overheated.LOGGER.info("Registering Default Diode Heaters");
        registerHeater(AllBlocks.BLAZE_BURNER.get(), (level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.NONE) {
                return HeatData.empty();
            }
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) {
                return new HeatData(0,1,0,0);
            }
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
                return new HeatData(1,0,0,0);
            }
            return HeatData.empty();
        });

        registerHeater(Blocks.FURNACE, (level, pos, state) -> {
            Boolean lit = state.getValue(AbstractFurnaceBlock.LIT);
            if (lit){
                return new HeatData(3,0,0,0);
            }
            return HeatData.empty();
        });

        registerHeater(Blocks.BLAST_FURNACE, (level, pos, state) -> {
            Boolean lit = state.getValue(AbstractFurnaceBlock.LIT);
            if (lit){
                return new HeatData(6,0,0,0);
            }
            return HeatData.empty();
        });

        registerHeater(Blocks.SMOKER, (level, pos, state) -> {
            Boolean lit = state.getValue(AbstractFurnaceBlock.LIT);
            if (lit){
                return new HeatData(6,0,0,0);
            }
            return HeatData.empty();
        });

        registerHeater(net.ironf.overheated.AllBlocks.SOLAR_PANEL.get(), ((level, pos, state) -> {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SolarPanelBlockEntity){
                return ((SolarPanelBlockEntity) be).getRecentReading();
            }
            return HeatData.empty();
        }));

        //registerHeater(Blocks.COMMAND_BLOCK, (level, pos, state) -> new HeatData(0,0,1,0));



    }
    public interface Heater {
        HeatData getActiveHeat(Level level, BlockPos pos, BlockState state);
    }



}
