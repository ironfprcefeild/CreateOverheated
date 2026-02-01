package net.ironf.overheated.nuclear.radiation;

import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RadiationMap {
    public static HashMap<Long,Integer> RadiationHashMap = new HashMap<>();

    public static void subscribeEvents(IEventBus eventBus){
        eventBus.register(RadiationMap.class);
    }

    @SubscribeEvent
    public static void saveChunkData(ChunkDataEvent.Save event){
        CompoundTag data = event.getData();
        Long myPos = event.getChunk().getPos().toLong();
        if (RadiationHashMap.containsKey(myPos)){
            data.putInt("r"+(myPos),RadiationHashMap.get(myPos));
        }
        //Overheated.LOGGER.info("Saved Chunk Data");
    }

    @SubscribeEvent
    public static void loadChunkData(ChunkDataEvent.Load event){
        CompoundTag data = event.getData();
        Long myPos = event.getChunk().getPos().toLong();
        String dataAddress = "r"+myPos;
        if (data.contains(dataAddress)){
            RadiationHashMap.put(myPos,data.getInt(dataAddress));
        }
        //Overheated.LOGGER.info("Loaded Chunk Data");
    }

    @SubscribeEvent
    public static void unloadChunkData(ChunkEvent.Unload event){
        //RadiationHashMap.remove(event.getChunk().getPos().toLong());
        //Overheated.LOGGER.info("Unloaded Chunk Data");
    }

    /// Modifying Rad Map Utility Methods
    public static int getRadiationIn(ChunkPos chunk){
        return RadiationHashMap.getOrDefault(chunk.toLong(),0);
    }
    public static void setRadiationIn(ChunkPos chunk, Integer newRadiation){
        RadiationHashMap.put(chunk.toLong(),newRadiation);
    }

    //This creates a pulse of radiation that spreads out, with diminsihing adds.
    //E.X. a pulse of strength 2 would add 2r to Chunk, and 1r to all adjacent chunks. 3 would spread further.
    public static void pulseRadiation(Level level, BlockPos pos, int strength){
        pulseRadiation(level.getChunk(pos).getPos(),strength,Math.signum(strength) < 0);
    }

    public static void pulseRadiation(ChunkPos chunk, int strength, boolean isAntiRad){
        setRadiationIn(chunk,strength);
        strength--;

        ArrayList<ChunkPos> edgeChunks = new ArrayList<>();
        ArrayList<ChunkPos> visited = new ArrayList<>();
        ArrayList<ChunkPos> newEdgeChunks = new ArrayList<>();
        edgeChunks.add(chunk);
        visited.add(chunk);

        int sign = isAntiRad ? -1 : 1;
        while (strength > 0) {
            for (ChunkPos c : edgeChunks){
                for (Vec2 d : chunkDirections){
                    ChunkPos spreadTo = chunkRelativeTo(c,d);
                    if (!edgeChunks.contains(spreadTo) && !visited.contains(spreadTo)){
                        newEdgeChunks.add(spreadTo);
                        visited.add(spreadTo);
                        setRadiationIn(spreadTo,getRadiationIn(spreadTo)+(sign * strength));
                    }
                }
            }
            edgeChunks.clear();
            edgeChunks.addAll(newEdgeChunks);
            newEdgeChunks.clear();

            strength--;
        }

    }

    public static Vec2[] chunkDirections = {
            new Vec2(0,1),
            new Vec2(0,-1),
            new Vec2(1,0),
            new Vec2(1,-1),
            new Vec2(1,1),
            new Vec2(-1,0),
            new Vec2(-1,1),
            new Vec2(-1,-1)
    };
    public static ChunkPos chunkRelativeTo(ChunkPos chunk, Vec2 d){
        return new ChunkPos((int) (chunk.x+d.x), (int) (chunk.z+d.y));
    }

    //TODO make radiation decay over time
    //TODO make radiation cause negative effects
}
