package net.ironf.overheated.worldgen.saltCaves;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;

public class SaltCaveFeature extends Feature<NoneFeatureConfiguration> {

    public SaltCaveFeature(Codec<NoneFeatureConfiguration> codec, int sizeLower, int sizeUpper, int frequency, float crystalFrequency, int shellHeight, BlockEntry<? extends Block> crystalBlock, BlockEntry<? extends Block> shellBlock) {
        super(codec);
        this.sizeLower = sizeLower;
        this.sizeUpper = sizeUpper;
        this.frequency = frequency;
        this.crystalFrequency = crystalFrequency;
        this.shellHeight = shellHeight;
        this.crystalBlock = crystalBlock;
        this.shellBlock = shellBlock;
    }

    public final int sizeLower;
    public final int sizeUpper;
    public final int frequency;
    public final float crystalFrequency;
    public final int shellHeight;

    public final BlockEntry<? extends Block> crystalBlock;
    public final BlockEntry<? extends Block> shellBlock;

    public void addShellAt(BlockPos pos, Level level, RandomSource rand, ArrayList<BlockPos> crystalOrigins){
        level.setBlock(pos, shellBlock.getDefaultState(), 2);
        if (rand.nextFloat() < crystalFrequency){
            crystalOrigins.add(pos);
        }
        BlockPos upperBlock = pos.relative(Direction.UP,shellHeight*2);
        level.setBlock(upperBlock,shellBlock.getDefaultState(),2);
        if (rand.nextFloat() < crystalFrequency){
            crystalOrigins.add(pos);
        }
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource rand = context.random();
        if (rand.nextIntBetweenInclusive(0, frequency) == frequency){
            BlockPos pos = context.origin();
            WorldGenLevel level = context.level();
            int size = rand.nextIntBetweenInclusive(sizeLower,sizeUpper);
            ArrayList<BlockPos> metBlocks = new ArrayList<>(size);
            ArrayList<BlockPos> availableBlocks = new ArrayList<>();
            ArrayList<BlockPos> crystalOrigins = new ArrayList<>();
            //Make Base
            for(int i = 0; i < size; i++) {
                //Add Blocks
                level.setBlock(pos, shellBlock.getDefaultState(), 2);
                if (rand.nextFloat() < crystalFrequency){
                    crystalOrigins.add(pos);
                }
                BlockPos upperBlock = pos.relative(Direction.UP,shellHeight*2);
                level.setBlock(upperBlock,shellBlock.getDefaultState(),2);
                if (rand.nextFloat() < crystalFrequency){
                    crystalOrigins.add(pos);
                }

                //Carve
                BlockPos upTower = pos;
                for (int u = (shellHeight -1) * 2 ; u > 0; u--){
                    upTower = upTower.above();
                    level.setBlock(upTower, Blocks.AIR.defaultBlockState(),2);
                }

                //Update Availability
                metBlocks.add(pos);
                for (Direction d : Iterate.horizontalDirections){
                    availableBlocks.add(pos.relative(d));
                }
                availableBlocks.removeAll(metBlocks);

                //Select New Block
                pos = availableBlocks.get(rand.nextIntBetweenInclusive(0,availableBlocks.size() - 1));
            }

            //Build Outer Shell
            ArrayList<BlockPos> nextBlocks = new ArrayList<>();
            ArrayList<BlockPos> currentBlocks = availableBlocks;
            for (int o = 0; o < shellHeight - 1; o++) {
                for (BlockPos bPos : currentBlocks) {
                    //Add Blocks
                    BlockPos lowerBlock = pos.relative(Direction.UP,o);
                    level.setBlock(lowerBlock, shellBlock.getDefaultState(), 2);
                    if (rand.nextFloat() < crystalFrequency){
                        crystalOrigins.add(lowerBlock);
                    }


                    BlockPos upperBlock = pos.relative(Direction.UP,shellHeight*2 - o);
                    level.setBlock(upperBlock,shellBlock.getDefaultState(),2);
                    if (rand.nextFloat() < crystalFrequency){
                        crystalOrigins.add(upperBlock);
                    }

                    //Carve
                    BlockPos upTower = lowerBlock;
                    for (int u = (shellHeight-1-o) * 2 ; u > 0; u--){
                        upTower = upTower.above();
                        level.setBlock(upTower, Blocks.AIR.defaultBlockState(),2);
                    }

                    //Modify Lists
                    metBlocks.add(bPos);
                    for (Direction d : Iterate.horizontalDirections) {
                        nextBlocks.add(bPos.relative(d));
                    }
                    nextBlocks.removeAll(metBlocks);
                }
                currentBlocks = nextBlocks;
                nextBlocks = new ArrayList<>();
            }


            //Add Crystals

            return true;
        } else {
            return false;
        }
    }



}
