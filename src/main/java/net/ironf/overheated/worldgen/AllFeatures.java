package net.ironf.overheated.worldgen;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.ironf.overheated.worldgen.bedrockDeposits.BedrockDepositFeature;
import net.minecraftforge.registries.RegistryObject;

import static net.ironf.overheated.Overheated.REGISTRATE;
public class AllFeatures {
    public static void register(){

    }

    //This is a placeholder registration
    public static final RegistryObject<BedrockDepositFeature> morkite = REGISTRATE.depositFeature("morkite_deposit")
            .Frequency(2)
            .Size(10,20)
            .BorderSize(3)
            .makeBlock(AllBlocks.ANTI_LASER_PLATING)
            .makeEncasedBlock(com.simibubi.create.AllBlocks.DEEPSLATE_ZINC_ORE).register();


}
