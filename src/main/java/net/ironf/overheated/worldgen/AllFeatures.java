package net.ironf.overheated.worldgen;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.ironf.overheated.worldgen.bedrockDeposits.BedrockDepositFeature;
import net.ironf.overheated.worldgen.saltCaves.SaltCaveFeature;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.RegistryObject;

import static net.ironf.overheated.Overheated.REGISTRATE;
import static net.ironf.overheated.utility.registration.OverheatedRegistrate.FEATURES;

public class AllFeatures {
    public static void register(){

    }

    //This is a placeholder registration
    public static final RegistryObject<BedrockDepositFeature> morkite = REGISTRATE.depositFeature("morkite_deposit")
            .Frequency(256)
            .Size(10,20)
            .BorderSize(3)
            .makeBlock(AllBlocks.ANTI_LASER_PLATING)
            .makeEncasedBlock(com.simibubi.create.AllBlocks.DEEPSLATE_ZINC_ORE).register();

    public static final RegistryObject<SaltCaveFeature> pureSaltCave = REGISTRATE.saltCaveFeature("pure_salt_cave")
            .Frequency(2)
            .Size(40,50)
            .shellHeight(12)
            .makeCrystalBlock(com.simibubi.create.AllBlocks.RAW_ZINC_BLOCK)
            .makeShellBlock(com.simibubi.create.AllBlocks.ROSE_QUARTZ_BLOCK)
            .crystalFrequency(0.1f)
            .crystalSizes(3,10)
            .register();

}
