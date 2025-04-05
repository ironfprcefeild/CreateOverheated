package net.ironf.overheated.cooling.chillChannel.network;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface IChillChannelHook {
    int getBlockCapacity();
    float getCoolingUnitsCapacity();

    //Coolant Qualities
    float getNetworkEfficency();
    float getNetworkMinTemp();

    default boolean canBeRouted(){
        return true;
    }

    void setHookTarget(BlockPos bp);

    default void sendParticlesToo(BlockPos bp, SmartBlockEntity me){
        Level level = me.getLevel();
        BlockPos sourcePos = me.getBlockPos();
        double distance = sourcePos.distSqr(bp);
        double vx = (sourcePos.getX() - bp.getX())/distance;
        double vy = (sourcePos.getY() - bp.getY())/distance;
        double vz = (sourcePos.getZ() - bp.getZ())/distance;
        level.addParticle(ParticleTypes.ELECTRIC_SPARK, bp.getX() + 0.5, bp.getY() + 1.5, bp.getZ() + 0.5, vx, vy, vz);
        level.addParticle(ParticleTypes.END_ROD, bp.getX() + 0.5, bp.getY() + 1.5, bp.getZ() + 0.5, vx, vy, vz);

    }

}
