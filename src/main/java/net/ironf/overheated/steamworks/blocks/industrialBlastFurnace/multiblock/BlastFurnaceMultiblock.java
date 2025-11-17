package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock;

import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static net.ironf.overheated.AllTags.AllBlockTags.*;

public class BlastFurnaceMultiblock {
    /// Figuring this out was really tricky,
    /// and I found it really helpful to look through SlimeKnight's T-construct code
    /// This isn't a direct copy but there are lots of similarities is the larger structure


    //Status
    public MultiblockResult status;
    public void setError(BlockPos pos, String key){
        status = MultiblockResult.ERROR(pos, ("block.coverheated.multiblock.error."+key));
    }
    public void setNoError(){
        status = MultiblockResult.VALID;
    }

    //Helper Values
    private static final int NORTH = Direction.NORTH.get2DDataValue();
    private static final int EAST = Direction.EAST.get2DDataValue();
    private static final int SOUTH = Direction.SOUTH.get2DDataValue();
    private static final int WEST = Direction.WEST.get2DDataValue();

    public static boolean isAir(BlockState b){
        return b.isAir();
    }
    public static boolean isAir(Level level, BlockPos b){
        return isAir(level.getBlockState(b));
    }

    public static boolean isValidBlock(Level level, BlockPos b, BlockPos controllerPosition, ArrayList<BlockPos> servantList){
        BlockState bs = level.getBlockState(b);
        if (IBF_VALID.matches(bs)){
            if (IBF_SERVANT.matches(bs)) {
                servantList.add(b);
            }
            return true;
        //We have this else because there should only be one controller per multiblock
        //The controller block IS NOT in the IBF valid tag,
        // so if we find a non-matching block, only return true if we are at the controller position
        } else return b == controllerPosition;
    }

    public static boolean isValidNonServant(Level level, BlockPos b){
        BlockState bs = level.getBlockState(b);
        return  (IBF_VALID.matches(bs) && !IBF_VALID.matches(bs));
    }


    //Assembling Multiblock
    public MultiblockData assembleMultiblock(Level level, BlastFurnaceControllerBlockEntity be){
        return assembleMultiblock(level,be.getBlockPos(),level.getBlockState(be.getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    public MultiblockData assembleMultiblock(Level level, BlockPos controllerPosition){
        return assembleMultiblock(level,controllerPosition,level.getBlockState(controllerPosition).getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
    private BlockPos controllerPosition;
    public MultiblockData assembleMultiblock(Level level, BlockPos controllerPosition, Direction facing){
        this.controllerPosition = controllerPosition;
        ///Make arraylist to store servants
        ArrayList<BlockPos> servantPositions = new ArrayList<>();

        /// Find the proper center
        BlockPos center = controllerPosition.relative(facing.getOpposite());
        if (!isAir(level,center)){
            //Block Behind the controller is not empty, we are either on the floor or this is invalid
            //So we try moving it up one, if it is still invalid we throw an error
            center = center.above();
            if (!isAir(level,center)){
                setError(center,"invalid_inner");
                return null;
            }
        }

        ///Now we find the distances of each wall from the center
        //south/west/north/east
        int[] edges = new int[4];
        for (Direction d : Iterate.horizontalDirections){
            BlockPos pos = center;
            for (int i = 0; i < 32 && level.isLoaded(pos) && isAir(level,pos); i++) {
                pos = pos.relative(d);
            }
            edges[d.get2DDataValue()] = (pos.getX() - center.getX()) + (pos.getZ() - center.getZ());
        }

        //Make sure it is not too large
        if (edges[SOUTH] + edges[NORTH] - 1 > 32 || edges[EAST] + edges[WEST] - 1 > 32) {
            setError(null,"too_large");
            return null;
        }

        ///Make Corner Pos (corners of the middle layer, where middle is he same layer as center (which is usally the controller))
        BlockPos from = center.offset(edges[WEST],0,edges[NORTH]);
        BlockPos to = center.offset(edges[EAST],0,edges[SOUTH]);

        ///Check the "middle" layer
        MultiblockResult MiddleResult = checkLayer(level, from, to, false, false,servantPositions);
        if (!MiddleResult.success()) {
            this.status = MiddleResult;
            return null;
        } else {
            setNoError();
        }

        ///Check downwards till we reach the floor.
        // floorOffset is incremented one more time than is valid, so it actually finds the floor
        int floorOffset = 1;
        while (floorOffset < 32 && checkLayer(level,from.below(floorOffset),to.below(floorOffset),false,false,servantPositions) == MultiblockResult.VALID){
            floorOffset++;
        }

        ///Now check for the floor, if the floor is invalid, we throw an error
        MultiblockResult FloorResult = checkLayer(level,from.below(floorOffset),to.below(floorOffset),true,false,servantPositions);
        if (!FloorResult.success()){
            this.status = FloorResult;
            return null;
        } else {
            setNoError();
        }

        ///Now move up until we find the top of the structure
        //Top Offset will be equal to the number of blocks needed to move up to reach the gas escape position
        //The gas escape zone must be fully empty on inner blocks, but does not care about edges
        int topOffset = 1;
        while (topOffset < 32 && checkLayer(level,from.above(topOffset),to.above(topOffset),false,false,servantPositions) == MultiblockResult.VALID){
            topOffset++;
        }

        /// Make sure it is not too tall
        if (topOffset + floorOffset -1 > 32){
            setError(null,"too_tall");
            return null;
        }

        ///Check for clearance for gas escape
        MultiblockResult topResult = checkLayer(level,from.above(topOffset),to.above(topOffset),false,true,servantPositions);
        if (!topResult.success()){
            this.status = topResult;
            return null;
        } else {
            setNoError();
        }

        ///Calculate final bounds
        BlockPos maxPos = to.above(topOffset-1);
        BlockPos minPos = from.below(floorOffset);

        /// Validate Servants
        for (BlockPos servantPos : servantPositions){
            //Fluid Drains / Arc Attachments are fine where ever
            //Steam Intakes must be on the bottom layer
            //Item Ducts must be on the top
            BlockState bs = level.getBlockState(servantPos);
            //0 = a middle layer, -1 = the bottom, 1 = the top.
            int layerType = (servantPos.getY() == maxPos.getY()) ? (1) : (servantPos.getY() == minPos.getY() ? -1 : 0);
            if ((IBF_SERVANT_TOP.matches(bs) && layerType != 1) || (IBF_SERVANT_BOTTOM.matches(bs) && layerType != -1)){
                setError(servantPos,"servant_wrong_layer");
            }
        }


        return new MultiblockData(minPos,maxPos, (BlastFurnaceControllerBlockEntity) level.getBlockEntity(controllerPosition),servantPositions);

    }

    ///Checks a single layer
    // Do not set both floorLayer and IgnoreEdges to true.
    public MultiblockResult checkLayer(Level level, BlockPos from, BlockPos to, boolean floorLayer, boolean ignoreEdges, ArrayList<BlockPos> servantPositions){
       //Make sure it is loaded
        if (!level.hasChunksAt(from, to)) {
            return MultiblockResult.ERROR(null,"not_loaded");
        }

        //Check all the inside blocks that should be air/valid
        int y = from.getY();
        if (!floorLayer) {
            for (int x = from.getX() + 1; x < to.getX(); x++) {
                for (int z = from.getZ() + 1; z < to.getZ(); z++) {
                    if (!isAir(level, new BlockPos(x, y, z))) {
                        return MultiblockResult.ERROR(new BlockPos(x, y, z), ignoreEdges ? "no_gas_escape" : "invalid_inner");
                    }
                }
            }
        } else {
            for (int x = from.getX() + 1; x < to.getX(); x++) {
                for (int z = from.getZ() + 1; z < to.getZ(); z++) {
                    if (!isValidNonServant(level, new BlockPos(x, y, z))) {
                        return MultiblockResult.ERROR(new BlockPos(x, y, z), "invalid_inner_floor");
                    }
                }
            }
        }

        if (ignoreEdges){
            //If we are ignoring edges, we can return right now!
            return MultiblockResult.VALID;
        }

        //Create Candidate Servant Positions
        ArrayList<BlockPos> candidateServants = new ArrayList<>();

        //Check Edges
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        Predicate<BlockPos> wallCheck = pos -> isValidBlock(level, pos,controllerPosition,candidateServants);
        for (int x = from.getX(); x < to.getX(); x++) {
            if (!wallCheck.test(mutable.set(x, y, from.getZ()))) return MultiblockResult.ERROR(mutable.immutable(), "invalid_wall_block");
            if (!wallCheck.test(mutable.set(x, y, to.getZ()))) return MultiblockResult.ERROR(mutable.immutable(), "invalid_wall_block");
        }
        for (int z = from.getZ(); z < to.getZ(); z++) {
            if (!wallCheck.test(mutable.set(from.getX(), y, z))) return MultiblockResult.ERROR(mutable.immutable(), "invalid_wall_block");
            if (!wallCheck.test(mutable.set(to.getX(), y, z))) return MultiblockResult.ERROR(mutable.immutable(), "invalid_wall_block");
        }


        servantPositions.addAll(candidateServants);
        removeDuplicates(servantPositions);
        return MultiblockResult.VALID;
    }

    public void removeDuplicates(ArrayList<BlockPos> arrayList){
        Set<BlockPos> set = new HashSet<>(arrayList);
        arrayList.clear();
        arrayList.addAll(set);
    }

}
