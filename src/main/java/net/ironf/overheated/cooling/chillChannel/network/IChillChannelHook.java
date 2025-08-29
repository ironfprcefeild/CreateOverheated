package net.ironf.overheated.cooling.chillChannel.network;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public interface IChillChannelHook {
    int getBlockCapacity();
    float getCoolingUnitsCapacity();

    //Coolant Qualities
    float getNetworkEfficency();
    float getNetworkMinTemp();

    //Indicates if a block can accept a channel, is only overridden by the core block.
    default boolean canBeRouted(){
        return true;
    }

    void setDrawFrom(BlockPos bp);
    void unsetDrawFrom();
    void setSendToo(BlockPos bp);

    default void sendParticlesToo(BlockPos bp, SmartBlockEntity me){
        Level level = me.getLevel();
        BlockPos sourcePos = me.getBlockPos();
        double vx = (sourcePos.getX() - bp.getX())/10f;
        double vy = (sourcePos.getY() - bp.getY())/10f;
        double vz = (sourcePos.getZ() - bp.getZ())/10f;
        level.addParticle(ParticleTypes.ELECTRIC_SPARK, bp.getX() + 0.5, bp.getY() + 1.5, bp.getZ() + 0.5, vx, vy, vz);
        level.addParticle(ParticleTypes.END_ROD, bp.getX() + 0.5, bp.getY() + 1.5, bp.getZ() + 0.5, vx, vy, vz);

    }

}
