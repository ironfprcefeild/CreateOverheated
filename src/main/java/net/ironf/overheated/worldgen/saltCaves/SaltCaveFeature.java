package net.ironf.overheated.worldgen.saltCaves;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;

public class SaltCaveFeature extends Feature<NoneFeatureConfiguration> {

    public SaltCaveFeature(Codec<NoneFeatureConfiguration> codec, int sizeLower, int sizeUpper, int frequency, float crystalFrequency, int shellHeight, int crystalSizeUpper, int crystalSizeLower, BlockEntry<? extends Block> crystalBlock, BlockEntry<? extends Block> shellBlock) {
        super(codec);
        this.sizeLower = sizeLower;
        this.sizeUpper = sizeUpper;
        this.frequency = frequency;
        this.crystalFrequency = crystalFrequency;
        this.shellHeight = shellHeight;
        this.crystalSizeUpper = crystalSizeUpper;
        this.crystalSizeLower = crystalSizeLower;
        this.crystalBlock = crystalBlock;
        this.shellBlock = shellBlock;
    }

    public final int sizeLower;
    public final int sizeUpper;
    public final int frequency;
    public final float crystalFrequency;
    public final int shellHeight;
    public final int crystalSizeUpper;
    public final int crystalSizeLower;

    public final BlockEntry<? extends Block> crystalBlock;
    public final BlockEntry<? extends Block> shellBlock;

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
                BlockPos upperBlock = pos.relative(Direction.UP,shellHeight*2 - 1);
                level.setBlock(upperBlock,shellBlock.getDefaultState(),2);
                crystalOrigins.add(pos);
                crystalOrigins.add(upperBlock);

                //Carve
                BlockPos upTower = pos;
                for (int u = (shellHeight -1) * 2; u > 0; u--){
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

            for (int o = 1; o < shellHeight ; o++) {
                for (BlockPos bPos : currentBlocks) {
                    //Add Blocks
                    BlockPos lowerBlock = bPos.relative(Direction.UP,o);
                    level.setBlock(lowerBlock, shellBlock.getDefaultState(), 2);
                    BlockPos upperBlock = bPos.relative(Direction.UP,shellHeight*2).relative(Direction.DOWN,o + 1);
                    level.setBlock(upperBlock,shellBlock.getDefaultState(),2);
                    crystalOrigins.add(lowerBlock);
                    crystalOrigins.add(upperBlock);

                    //Carve
                    BlockPos upTower = lowerBlock;
                    for (int u = (shellHeight-o-1) * 2; u > 0; u--){
                        upTower = upTower.above();
                        level.setBlock(upTower, Blocks.AIR.defaultBlockState(),2);
                    }

                    //Modify Lists
                    metBlocks.add(bPos);
                    for (Direction d : Iterate.horizontalDirections) {
                        nextBlocks.add(bPos.relative(d));
                    }
                }
                nextBlocks.removeAll(metBlocks);
                currentBlocks = nextBlocks;
                nextBlocks = new ArrayList<>();
            }


            ////Add Crystals

            //Find Sprout-able Blocks
            ArrayList<vectorPos> crystalSprouts = new ArrayList<>();
            for (BlockPos crystal : crystalOrigins){
                for (Direction d : Iterate.directions){
                    crystalSprouts.add(new vectorPos(crystal,d));
                }
            }

            //Add crystals
            float startingSize = crystalSprouts.size();
            float addedCrystals = 0f;
            while (crystalFrequency > (addedCrystals/startingSize)){
                vectorPos currentCrystal = crystalSprouts.get(rand.nextIntBetweenInclusive(0,crystalSprouts.size() - 1));
                BlockPos crystalTower = currentCrystal.pos;
                for (int u = rand.nextIntBetweenInclusive(crystalSizeLower, crystalSizeUpper); u > 0; u--){
                    crystalTower = crystalTower.relative(currentCrystal.direction);
                    if (!level.getBlockState(crystalTower).isAir()){
                        break;
                    }
                    level.setBlock(crystalTower, crystalBlock.getDefaultState(),2);
                }
                addedCrystals++;
            }

            return true;
        } else {
            return false;
        }
    }

    public static class vectorPos{
        public BlockPos pos;
        public Direction direction;
        public vectorPos(BlockPos pos, Direction direction){
            this.pos = pos;
            this.direction =direction;
        }
    }


}
