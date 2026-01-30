package net.ironf.overheated.nuclear.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class RadiationMap extends SavedData {
    public static HashMap<Long,Integer> RadiationHashMap = new HashMap<>();

    /// Saving Data to the World
    public void setRadiationMap(HashMap<Long, Integer> radiationHashMap) {
        RadiationHashMap = radiationHashMap;
        this.setDirty();
    }

    public HashMap<Long, Integer> getRadiationHashMap() {
        return RadiationHashMap;
    }

    public static RadiationMap create(){
        return new RadiationMap();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString(RadiationHashMap.toString(),"radiation");
        this.setDirty();
        return tag;
    }

    public static RadiationMap load(CompoundTag tag){
        RadiationMap data = create();
        data.setRadiationMap(unstringHashMap(tag.getString("radiation")));
        return data;
    }


    public static RadiationMap manage(MinecraftServer server){
        return server.overworld().getDataStorage()
                .computeIfAbsent(RadiationMap::load, RadiationMap::create, "radiationmap");
    }

    /// Hashmap Untstringing
    private static HashMap<Long, Integer> unstringHashMap(String rawMap) {
        HashMap<Long, Integer> ToReturnMap = new HashMap<>();

        //Loop over the whole String
        int toPut;
        Long newKey;
        for (int i = 0; i != rawMap.length(); i++){

            //Each '=' means a new a key pair
            if (rawMap.charAt(i) == '='){
                //Move foward until you find a comma (meaning the end of the keypair)
                //a will be the location of the comma
                int a = i;
                while (rawMap.charAt(a) != ',' && rawMap.charAt(a) != '}'){
                    a++;
                }
                //This substring will be the value,
                //we have to add one to i because the first value is inclusive
                toPut = Integer.parseInt(rawMap.substring(i+1,a));
                if (toPut == 0){
                    continue;
                }
                //After this loop, a will be the location of the comma on the other side
                a = i;
                while (rawMap.charAt(a) != ',' && rawMap.charAt(a) != '{'){
                    a--;
                }
                //This substring will be the key
                //We have to add 1 to a because the first value is inclusive.
                newKey = Long.parseLong(rawMap.substring(a+1, i));

                //Add the new keypair
                ToReturnMap.put(newKey,toPut);
            }
        }
        //Return completed map
        return ToReturnMap;
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
        if (level.isClientSide) {return;}
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
