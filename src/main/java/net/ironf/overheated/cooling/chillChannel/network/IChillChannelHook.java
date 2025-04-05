package net.ironf.overheated.cooling.chillChannel.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

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


}
