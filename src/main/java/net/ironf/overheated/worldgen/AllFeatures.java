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
            .Frequency(300)
            .Size(10,20)
            .BorderSize(3)
            .makeBlock(AllBlocks.ANTI_LASER_PLATING)
            .makeEncasedBlock(com.simibubi.create.AllBlocks.DEEPSLATE_ZINC_ORE).register();

    //Deposits
    public static final RegistryObject<BedrockDepositFeature> nihilite = REGISTRATE.depositFeature("nihilite_deposit")
            .Frequency(300)
            .Size(10,22)
            .BorderSize(4)
            .makeBlock(AllBlocks.NIHILITE_DEPOSIT)
            .makeEncasedBlock(AllBlocks.NIHILISTONE).register();

    //Geothermal Vent
    public static final RegistryObject<BedrockDepositFeature> heated_geothermal = REGISTRATE.depositFeature("heated_geothermal_vent")
            .Frequency(300)
            .Size(15,25)
            .BorderSize(4)
            .makeBlock(AllBlocks.HEATED_VENT)
            .makeEncasedBlock(AllBlocks.GEOTHERMIUM).register();


    //Superheated Geothermal Vent
    public static final RegistryObject<BedrockDepositFeature> superheated_geothermal = REGISTRATE.depositFeature("superheated_geothermal_vent")
            .Frequency(384)
            .Size(15,25)
            .BorderSize(4)
            .makeBlock(AllBlocks.SUPERHEATED_VENT)
            .makeEncasedBlock(AllBlocks.NETHER_GEOTHERMIUM).register();


    //Salt Caves
    public static final RegistryObject<SaltCaveFeature> pureSaltCave = REGISTRATE.saltCaveFeature("pure_salt_cave")
            .Frequency(256)
            .Size(40,60)
            .shellHeight(12)
            .makeCrystalBlock(AllBlocks.WHITE_SALT_CRYSTAL)
            .makeShellBlock(AllBlocks.WHITE_SALT_BLOCK)
            .crystalFrequency(0.1f)
            .crystalSizes(3,9)
            .register();
    public static final RegistryObject<SaltCaveFeature> redSaltCave = REGISTRATE.saltCaveFeature("red_salt_cave")
            .Frequency(512)
            .Size(40,60)
            .shellHeight(13)
            .makeCrystalBlock(AllBlocks.RED_SALT_CRYSTAL)
            .makeShellBlock(AllBlocks.RED_SALT_BLOCK)
            .crystalFrequency(0.1f)
            .crystalSizes(3,9)
            .register();
    public static final RegistryObject<SaltCaveFeature> blueSaltCave = REGISTRATE.saltCaveFeature("blue_salt_cave")
            .Frequency(512)
            .Size(40,60)
            .shellHeight(12)
            .makeCrystalBlock(AllBlocks.BLUE_SALT_CRYSTAL)
            .makeShellBlock(AllBlocks.BLUE_SALT_BLOCK)
            .crystalFrequency(0.1f)
            .crystalSizes(4,10)
            .register();

}
