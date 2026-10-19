package net.ironf.overheated.metalWorking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

public class UniversalState {
    public final BlockState blockState;
    public final FluidState fluidState;

    public UniversalState(BlockState blockState, FluidState fluidState) {
        this.blockState = blockState;
        this.fluidState = fluidState;
    }

    public static UniversalState stateAt(Level level, BlockPos at){
        BlockState BSCandidate = level.getBlockState(at);
        FluidState FSCandidate = level.getFluidState(at);

        if (BSCandidate != Blocks.AIR.defaultBlockState()){
            return new UniversalState(BSCandidate, Fluids.EMPTY.defaultFluidState());
        } else if (FSCandidate != Fluids.EMPTY.defaultFluidState()){
            return new UniversalState(Blocks.AIR.defaultBlockState(), FSCandidate);
        }
        return empty;
    }

    public static UniversalState of(BlockState state){
        return new UniversalState(state,Fluids.EMPTY.defaultFluidState());
    }
    public static UniversalState of(FluidState state){
        return new UniversalState(Blocks.AIR.defaultBlockState(),state);
    }
    public static UniversalState of(Block block){
        return of(block.defaultBlockState());
    }
    public static UniversalState of(Fluid fluid){
        return of(fluid.defaultFluidState());
    }


    public static final UniversalState empty = new UniversalState(Blocks.AIR.defaultBlockState(),Fluids.EMPTY.defaultFluidState());
    public boolean empty(){
        return blockState == Blocks.AIR.defaultBlockState() && fluidState == Fluids.EMPTY.defaultFluidState();
    }

    public boolean matches(UniversalState compare){
        return blockState == compare.blockState && fluidState == compare.fluidState;
    }

    public boolean looseMatches(UniversalState compare){
        return blockState.getBlock() == compare.blockState.getBlock()
                && fluidState == compare.fluidState;
    }

    public void set(Level level, BlockPos at, int flags) {
        set(level,at,flags,false);
    }

    public void set(Level level, BlockPos at, int flags, boolean allowSetToAir){
        if (blockState != Blocks.AIR.defaultBlockState() || allowSetToAir){
            level.setBlock(at, blockState,flags);
        } else if (fluidState != Fluids.EMPTY.defaultFluidState()){
            level.setBlock(at, fluidState.createLegacyBlock(),flags);
        }
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public FluidState getFluidState() {
        return fluidState;
    }

    /// Codecs
    public static final MapCodec<UniversalState> MAP_CODEC = (RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockState.CODEC.optionalFieldOf("blockstate",Blocks.AIR.defaultBlockState()).forGetter(UniversalState::getBlockState),
            FluidState.CODEC.optionalFieldOf("fluidstate",Fluids.EMPTY.defaultFluidState()).forGetter(UniversalState::getFluidState))
            .apply(inst,UniversalState::new)));
    public static final Codec<UniversalState> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf,UniversalState> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, UniversalState>() {
        @Override
        public UniversalState decode(RegistryFriendlyByteBuf buf) {
            return new UniversalState(
                    ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).decode(buf),
                    ByteBufCodecs.idMapper(Fluid.FLUID_STATE_REGISTRY).decode(buf));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UniversalState universalState) {
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).encode(buf,universalState.blockState);
            ByteBufCodecs.idMapper(Fluid.FLUID_STATE_REGISTRY).encode(buf,universalState.fluidState);
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<UniversalState>> STEAM_CODEC_LIST = new StreamCodec<>() {
        public NonNullList<UniversalState> decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<UniversalState> output = NonNullList.withSize(size,empty);
            for (int i = 0; i < size; i++) {
                output.set(i, STREAM_CODEC.decode(buf));
            }
            return output;
        }

        public void encode(RegistryFriendlyByteBuf buf, NonNullList<UniversalState> list) {
            //Size
            buf.writeInt(list.size());
            //Entries
            for (UniversalState state : list){
                STREAM_CODEC.encode(buf,state);
            }
        }
    };

    @Override
    public String toString() {
        return "[vBlockState: [" + blockState + "] FluidState: [" + fluidState + "]]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UniversalState otherState){
            return otherState.blockState == blockState && otherState.fluidState == fluidState;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockState, fluidState);
    }
}

