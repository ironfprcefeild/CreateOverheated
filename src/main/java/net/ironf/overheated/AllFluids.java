package net.ironf.overheated;

import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;

import static net.ironf.overheated.Overheated.REGISTRATE;


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

    public static final OverheatedRegistrate.FluidRegistration STRAY_SPIRIT =
            REGISTRATE.SimpleFluid("stray_spirit")
                    .levelDecreasePerBlock(2).tickRate(25).explosionResistance(100f).slopeFindDistance(3)
                    .Register(p -> p.canHydrate(true).canDrown(true).canSwim(true).canExtinguish(true)
                            .density(2000).viscosity(1400));

    public static final OverheatedRegistrate.FluidRegistration SLUDGE =
            REGISTRATE.SimpleFluid("sludge")
                    .tintColor(0x25BE45)
                    .levelDecreasePerBlock(2).tickRate(25).explosionResistance(100f).slopeFindDistance(3)
                    .Register(p -> p.canHydrate(false)
                                    .canDrown(true)
                                    .canSwim(true)
                                    .canExtinguish(true)
                                    .density(2000)
                                    .viscosity(1400));

    public static final OverheatedRegistrate.FluidRegistration PURIFIED_WATER =
            REGISTRATE.SimpleFluid("purified_water")
                    .tintColor(0x33B3FF)
                    .levelDecreasePerBlock(2)
                    .tickRate(20)
                    .explosionResistance(10f)
                    .slopeFindDistance(6)
                    .Register(p -> p.canHydrate(false)
                            .canDrown(true)
                            .canSwim(true)
                            .canExtinguish(true));

    public static final OverheatedRegistrate.FluidRegistration BLAZE_NECTAR =
            REGISTRATE.SimpleFluid("blaze_nectar")
                    .tintColor(0x553E9B)
                    .levelDecreasePerBlock(2)
                    .tickRate(15)
                    .explosionResistance(100f)
                    .slopeFindDistance(6)
                    .Register(p -> p.canHydrate(false)
                            .canDrown(true)
                            .canSwim(true)
                            .canExtinguish(false)
                            .lightLevel(8)
                            .temperature(30)
                            .supportsBoating(false));

    public static final OverheatedRegistrate.FluidRegistration LIQUID_NIHILITE =
            REGISTRATE.SimpleFluid("liquid_nihilite")
                    .tintColor(0x553E9B)
                    .levelDecreasePerBlock(2)
                    .tickRate(10)
                    .explosionResistance(200f)
                    .slopeFindDistance(6)
                    .Register(p -> p.canDrown(true));

}
