package net.ironf.overheated.laserOptics.mirrors;

import com.simibubi.create.foundation.utility.AttachedRegistry;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class mirrorRegister {

    //This is an adapted style of creates Boiler Heater Code
    private static final AttachedRegistry<Block, Reflector> MIRRORS = new AttachedRegistry<>(ForgeRegistries.BLOCKS);

    public static void registerReflector(ResourceLocation block, Reflector reflector) {
        MIRRORS.register(block, reflector);
    }

    public static void registerReflector(Block block, Reflector reflector) {
        MIRRORS.register(block,reflector);
    }

    public static Direction doReflection(Direction incoming, Level level, BlockPos pos, BlockState state, HeatData heatData) {
        Reflector mirror = MIRRORS.get(state.getBlock());
        if (mirror != null) {
            return mirror.doReflection(incoming,level, pos, state,heatData);
        }

        return incoming;
    }

    public static boolean isMirror(BlockState state){
        return  MIRRORS.get(state.getBlock()) != null;
    }



    public static Direction doReflection(Direction incoming, Level level, BlockPos pos, HeatData heatData) {
        return doReflection(incoming,level,pos,level.getBlockState(pos),heatData);
    }

    public static void registerDefaults(){
        Overheated.LOGGER.info("Registering Default Thermal Mirrors");

        registerReflector(AllBlocks.BASIC_MIRROR.get(), (incoming, level, pos, state, heat) -> {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            if (facing.getAxis() == incoming.getAxis()){
                return incoming;
            }
            return Direction.fromAxisAndDirection(findOtherAxis(incoming.getAxis(),facing.getAxis()),facing.getAxisDirection());
        });
        registerReflector(AllBlocks.SUPERHEAT_DIMMER.get(), (incoming, level, pos, state, heat) -> {
            if (heat.SuperHeat > 0 && heat.SuperHeat < 1){
                heat.Heat = heat.SuperHeat * 4;
                heat.SuperHeat = 0;
            } else {
                heat.expandSuperHeat(1);
            }
            return incoming;
        });
        registerReflector(AllBlocks.OVERHEAT_DIMMER.get(), (incoming, level, pos, state, heat) -> {
            if (heat.OverHeat > 0 && heat.OverHeat < 1){
                heat.SuperHeat = heat.OverHeat * 4;
                heat.OverHeat = 0;
            } else {
                heat.expandOverHeat(1);
            }
            return incoming;
        });
        registerReflector(AllBlocks.LASER_FILM.get(), ((incoming, level, pos, state, passingData) -> incoming));
    }

    public interface Reflector {
        Direction doReflection(Direction incoming, Level level, BlockPos pos, BlockState state, HeatData passingData);
    }

    public static Direction.Axis findOtherAxis(Direction.Axis a, Direction.Axis b){
        switch (a) {
            case X -> {
                switch (b) {
                    case X -> {
                        return Direction.Axis.X;
                    }
                    case Y -> {
                        return Direction.Axis.Z;
                    }
                    case Z -> {
                        return Direction.Axis.Y;
                    }
                }
            }
            case Y -> {
                switch (b) {
                    case X -> {
                        return Direction.Axis.Z;
                    }
                    case Y -> {
                        return Direction.Axis.Y;
                    }
                    case Z -> {
                        return Direction.Axis.X;
                    }
                }
            }
            case Z -> {
                switch (b) {
                    case X -> {
                        return Direction.Axis.Y;
                    }
                    case Y -> {
                        return Direction.Axis.X;
                    }
                    case Z -> {
                        return Direction.Axis.Z;
                    }
                }
            }
        }
        return a;
    }

}
