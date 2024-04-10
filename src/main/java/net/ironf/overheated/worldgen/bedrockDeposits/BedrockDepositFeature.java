package net.ironf.overheated.worldgen.bedrockDeposits;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;

public class BedrockDepositFeature extends Feature<NoneFeatureConfiguration> {

    public BedrockDepositFeature(Codec<NoneFeatureConfiguration> codec, int sizeLower, int sizeUpper, int frequency, BlockEntry<? extends Block> block, BlockEntry<? extends Block> encasedBlock) {
        super(codec);
        this.sizeLower = sizeLower;
        this.sizeUpper = sizeUpper;
        this.frequency = frequency;
        this.block = block;
        this.encasedBlock = encasedBlock;
    }

    public final int sizeLower;
    public final int sizeUpper;
    public final int frequency;


    public final BlockEntry<? extends Block> block;
    public final BlockEntry<? extends Block> encasedBlock;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource rand = context.random();
        if (rand.nextIntBetweenInclusive(0, frequency) == frequency){
            BlockPos pos = context.origin();
            WorldGenLevel level = context.level();
            int size = rand.nextIntBetweenInclusive(sizeLower,sizeUpper);
            ArrayList<BlockPos> metBlocks = new ArrayList<>(size);
            ArrayList<BlockPos> availableBlocks = new ArrayList<>();

            for(int i = 0; i < size; ++i) {
                level.setBlock(pos,block.getDefaultState(),2);
                BlockPos upTower = pos;

                //Create Tower
                for (int u = rand.nextIntBetweenInclusive(size, size * 3); u > 0; u--){
                    upTower = upTower.above();
                    level.setBlock(upTower,encasedBlock.getDefaultState(),2);

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

            return true;
        } else {
            return false;
        }
    }



}
