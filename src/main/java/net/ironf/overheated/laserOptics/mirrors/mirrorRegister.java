package net.ironf.overheated.laserOptics.mirrors;

import com.simibubi.create.foundation.utility.AttachedRegistry;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureHeater.PressureHeaterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class mirrorRegister {


    //Returning a direction indicates the new direction for the laser to travel
    //Modifying the heatdata changes the HD of the laser after it leaves
    //Returning null will stop the laser
    public interface Reflector {
        Direction doReflection(Direction incoming, Level level, BlockPos pos, BlockState state, HeatData passingData);
    }

    //Usage
    public static Direction doReflection(Direction incoming, Level level, BlockPos pos, BlockState state, HeatData heatData) {
        Reflector mirror = MIRRORS.get(state.getBlock());
        return mirror != null ? mirror.doReflection(incoming,level, pos, state,heatData) : incoming;
    }

    public static boolean isMirror(BlockState state){
        return  MIRRORS.get(state.getBlock()) != null;
    }

    public static Direction doReflection(Direction incoming, Level level, BlockPos pos, HeatData heatData) {
        return doReflection(incoming,level,pos,level.getBlockState(pos),heatData);
    }

    //This is an adapted style of creates Boiler Heater Code
    private static final AttachedRegistry<Block, Reflector> MIRRORS = new AttachedRegistry<>(ForgeRegistries.BLOCKS);

    public static void registerReflector(ResourceLocation block, Reflector reflector) {
        MIRRORS.register(block, reflector);
    }

    public static void registerReflector(Block block, Reflector reflector) {
        MIRRORS.register(block,reflector);
    }

    //Only use these register blocks whose block entity implements ILaserAbsorber or extend SmartLaserMachineBlockEntity
    public static void registerBEAbsorber(Block block){
        registerReflector(block, (incoming,level,pos,state,heat) -> {
            ILaserAbsorber be = ((ILaserAbsorber)(level.getBlockEntity(pos)));
            be.setLaserHD(heat,incoming);
            be.setLaserTimer(12,incoming);
            return null;
        });
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
                heat.Heat += heat.SuperHeat * 4;
                heat.SuperHeat = 0;
            } else {
                heat.expandSuperHeat(1);
            }
            return incoming;
        });
        registerReflector(AllBlocks.OVERHEAT_DIMMER.get(), (incoming, level, pos, state, heat) -> {
            if (heat.OverHeat > 0 && heat.OverHeat < 1){
                heat.SuperHeat += heat.OverHeat * 4;
                heat.OverHeat = 0;
            } else {
                heat.expandOverHeat(1);
            }
            return incoming;
        });


        registerReflector(AllBlocks.ANTI_LASER_PLATING.get(),(i,l,p,s,h) -> null);
        registerReflector(AllBlocks.LASER_FILM.get(), ((incoming, level, pos, state, passingData) -> incoming));


        //Block Entities
        registerBEAbsorber(AllBlocks.IMPACT_DRILL.get());
        registerBEAbsorber(AllBlocks.CHAMBER_CORE.get());
        registerReflector(AllBlocks.PRESSURE_HEATER.get(), (incoming,level,pos,state,heat) -> {
            PressureHeaterBlockEntity be = ((PressureHeaterBlockEntity) level.getBlockEntity(pos));
            be.laserHeatLevel = heat.useUpToOverHeat();
            be.laserTimer = 60;
            return incoming;
        });
        registerReflector(AllBlocks.BLAZE_CRUCIBLE.get(),(incoming,level,pos,state,beamHeat) -> {
            BlazeCrucibleBlockEntity be = ((BlazeCrucibleBlockEntity) level.getBlockEntity(pos));
            int newHeat = beamHeat.useUpToOverHeat();
            if (be.heatLevel != newHeat){
                be.needsStateUpdate = true;
            }
            be.heatLevel = newHeat;
            be.timeHeated = 15;
            return incoming;
        });
        registerReflector(AllBlocks.THERMOMETER.get(),(incoming,level,pos,state,heat) -> {
            ILaserAbsorber be = ((ILaserAbsorber)(level.getBlockEntity(pos)));
            be.setLaserHD(heat,incoming);
            be.setLaserTimer(12,incoming);
            return incoming;
        });
        //Replace all laser absorber blocks with the new smartlasermachine or a register here

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
