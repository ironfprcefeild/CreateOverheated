package net.ironf.overheated.worldgen.bedrockDeposits;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

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
            level.setBlock(pos,block.getDefaultState(),2);
            int size = rand.nextIntBetweenInclusive(sizeLower,sizeUpper);
            for(int i = 0; i < size; ++i) {
                pos = pos.relative(depositDirections[rand.nextIntBetweenInclusive(0,3)]);
                level.setBlock(pos,block.getDefaultState(),2);
                BlockPos upTower = pos;
                for (int u = 0; u < size / 2.5; u++){
                    upTower = upTower.above();
                    level.setBlock(upTower,encasedBlock.getDefaultState(),2);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static Direction[] depositDirections = {Direction.EAST,Direction.WEST,Direction.NORTH,Direction.EAST};


}
