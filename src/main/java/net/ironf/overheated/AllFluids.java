package net.ironf.overheated;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.Overheated.REGISTRATE;
import static net.ironf.overheated.utility.registration.OverheatedRegistrate.getFluidFactory;


public class AllFluids {
    public static void register(){
        AllSteamFluids.register();
        AllGasses.register();
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    /*
    IDs of registered Fluids:
    purified_water
    blaze_nectar
    sludge
    liquid_nihilite
    magmafreeze
     */

    public static final OverheatedRegistrate.FluidRegistration STRAY_SAUCE =
            REGISTRATE.SimpleFluid("stray_sauce")
                    .levelDecreasePerBlock(2).tickRate(25).explosionResistance(100f).slopeFindDistance(3)
                    .Register(p -> p.canHydrate(true).canDrown(true).canSwim(true).canExtinguish(true)
                            .density(2000).viscosity(1400));

    public static final FluidEntry<ForgeFlowingFluid.Flowing> SLUDGE =
            REGISTRATE.standardFluid("sludge")
                    .lang("Sludge")
                    .properties(b -> b.viscosity(2000)
                            .density(1400))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(ForgeFlowingFluid.Source::new)
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> PURIFIED_WATER =
            REGISTRATE.standardFluid("purified_water", getFluidFactory(
                            0x33B3FF, 1f / 8f * 2f))
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
            REGISTRATE.standardFluid("blaze_nectar",getFluidFactory(
                            0x553E9B, 1f / 10f * 2f, "blaze_nectar_flow"))
                    .lang("Blaze Nectar")
                    .source(ForgeFlowingFluid.Source::new)
                    .properties(p -> p.lightLevel(4).temperature(30).density(3).viscosity(100).supportsBoating(false))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(15)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> LIQUID_NIHILITE =
            REGISTRATE.standardFluid("liquid_nihilite", getFluidFactory(
                            0x553E9B, 1f / 10f * 2f))
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
