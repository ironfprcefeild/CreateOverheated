package net.ironf.overheated;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllFluids {
    public static void register(){
        AllSteamFluids.register();
        AllGasses.register();
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }
    public static final FluidEntry<ForgeFlowingFluid.Flowing> PURIFIED_WATER =
            REGISTRATE.standardFluid("purified_water", OverheatedRegistrate.SolidRenderedPlaceableFluidType.create(
                            0x33B3FF,
                            () -> 1f / 8f * 2f))
                    .lang("Purified Water")
                    .fluidProperties(p -> p.levelDecreasePerBlock(5)
                            .tickRate(20)
                            .slopeFindDistance(6)
                            .explosionResistance(10f))
                    .source(ForgeFlowingFluid.Source::new)
                    .bucket()
                    .build()
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BLAZE_NECTAR =
            REGISTRATE.standardFluid("blaze_nectar", OverheatedRegistrate.SolidRenderedPlaceableFluidType.create(
                            0xE99F19,
                            () -> 1f / 8f * 2f))
                    .lang("Blaze Nectar")
                    .source(ForgeFlowingFluid.Source::new)
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(15)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    //TODO add the rest of the coolants (snowy sludge, stray sauce, ghast gunk, and magmafreeze)
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SLUDGE =
            REGISTRATE.standardFluid("sludge", OverheatedRegistrate.SolidRenderedPlaceableFluidType.create(
                            0xE99F19,
                            () -> 1f / 8f * 2f))
                    .lang("Sludge")
                    .source(ForgeFlowingFluid.Source::new)
                    .fluidProperties(p -> p.levelDecreasePerBlock(4)
                            .tickRate(10)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> LIQUID_NIHILITE =
            REGISTRATE.standardFluid("liquid_nihilite", OverheatedRegistrate.SolidRenderedPlaceableFluidType.create(
                    0x553E9B,
                            () -> 1f / 10f * 2f))
                    .lang("Liquid Nihilite")
                    .source(ForgeFlowingFluid.Source::new)
                    .fluidProperties(p -> p.levelDecreasePerBlock(3)
                            .tickRate(10)
                            .slopeFindDistance(6)
                            .explosionResistance(200f))
                    .bucket()
                    .build()
                    .register();


}
