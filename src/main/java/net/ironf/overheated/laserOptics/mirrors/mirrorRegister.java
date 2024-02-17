package net.ironf.overheated.laserOptics.mirrors;

import com.simibubi.create.Create;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
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

    public static Direction doReflection(Direction incoming, Level level, BlockPos pos, BlockState state) {
        Reflector mirror = MIRRORS.get(state.getBlock());
        if (mirror != null) {
            return mirror.doReflection(incoming,level, pos, state);
        }

        return incoming;
    }

    public static boolean isMirror(BlockState state){
        return  MIRRORS.get(state.getBlock()) != null;
    }



    public static Direction doReflection(Direction incoming, Level level, BlockPos pos) {
        return doReflection(incoming,level,pos,level.getBlockState(pos));
    }

    public static void registerDefaults(){
        registerReflector(AllBlocks.BASIC_MIRROR.get(), (incoming, level, pos, state) -> {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            if (incoming.getAxis() == facing.getAxis())
                return incoming;

            return incoming.getClockWise(facing.getAxis());
        });
    }

    public interface Reflector {
        Direction doReflection(Direction incoming, Level level, BlockPos pos, BlockState state);
    }
}
