package net.ironf.overheated.gasses;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.core.Direction;

import static net.ironf.overheated.Overheated.REGISTRATE;
import static net.ironf.overheated.steamworks.AllSteamFluids.DISTILLED_WATER;

public class AllGasses {
    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }
    //TODO make proper texture for morkite and gasses instead of the placeholder in the resources right now
    // (Morkite is using steam bucket as placeholder)


    public static final OverheatedRegistrate.FluidRegistration morkite = REGISTRATE.SimpleFluid("morkite")

            .setGas(REGISTRATE.gasBlock("morkite")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(5,8)
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(8));

    public static final OverheatedRegistrate.FluidRegistration nihilite_gas = REGISTRATE.SimpleFluid("nihilite_gas")

            .setGas(REGISTRATE.gasBlock("nihilite_gas")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(2,8)
                    .explosionSafety(10)
                    .register())
            .Register(p -> p.density(8));

    public static final OverheatedRegistrate.FluidRegistration ammonia = REGISTRATE.SimpleFluid("ammonia")
            .tintColor(0xCF00C957)
            .overrideTexture("block/steam")
            .setGas(REGISTRATE.gasBlock("ammonia")
                    .defaultFlow(Direction.UP)
                    .overrideTexturing("block/steam")
                    .shiftChance(5)
                    .tickDelays(2,5)
                    .explosionSafety(12)
                    .register())
            .Register(p -> p.density(-3));

    public static final OverheatedRegistrate.FluidRegistration nitrogen = REGISTRATE.SimpleFluid("nitrogen")
            .tintColor(0xDDAF70A1)
            .overrideTexture("block/steam")
            .setGas(REGISTRATE.gasBlock("nitrogen")
                    .defaultFlow(Direction.UP)
                    .overrideTexturing("block/steam")
                    .shiftChance(10)
                    .tickDelays(1,4)
                    .explosionSafety(12)
                    .register())
            .Register(p -> p.density(-3));

    public static final OverheatedRegistrate.FluidRegistration cinderfume = REGISTRATE.SimpleFluid("cinderfume")
            .tintColor(0x33B3FF)
            .overrideTexture("steam")
            .setGas(REGISTRATE.gasBlock("cinderfume")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(3)
                    .tickDelays(2,5)
                    .explosionSafety(12)
                    .register())
            .Register(p -> p.density(4));

    public static final OverheatedRegistrate.FluidRegistration voidaium = REGISTRATE.SimpleFluid("voidaium")
            .tintColor(0x33B3FF)
            .overrideTexture("steam")
            .setGas(REGISTRATE.gasBlock("voidaium")
                    .defaultFlow(Direction.UP)
                    .shiftChance(5)
                    .tickDelays(1,2)
                    .explosionSafety(12)
                    .register())
            .Register(p -> p.density(-8));

    public static final OverheatedRegistrate.FluidRegistration water_vapor = REGISTRATE.SimpleFluid("water_vapor")
            .makeGasUnCapturable()
            .setGas(REGISTRATE.gasBlock("water_vapor")
                    .defaultFlow(Direction.UP)
                    .shiftChance(0)
                    .tickDelays(2,3)
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(-1));

    public static final OverheatedRegistrate.FluidRegistration oxygen = REGISTRATE.SimpleFluid("oxygen")
            .overrideTexture("block/steam")
            .makeGasUnCapturable()
            .tintColor(0xD77793FB)
            .setGas(REGISTRATE.gasBlock("oxygen")
                    .defaultFlow(Direction.UP)
                    .overrideTexturing("block/steam")
                    .shiftChance(3)
                    .tickDelays(1,5)
                    .passThroughPredicate(state -> state.isAir() || state.is(DISTILLED_WATER.FLUID_BLOCK.get()))
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(-1));

    public static final OverheatedRegistrate.FluidRegistration hydrogen = REGISTRATE.SimpleFluid("hydrogen")
            .tintColor(0xDCDD0000)
            .overrideTexture("block/steam")
            .makeGasUnCapturable()
            .setGas(REGISTRATE.gasBlock("hydrogen")
                    .defaultFlow(Direction.UP)
                    .shiftChance(5)
                    .tickDelays(1,3)
                    .explosionSafety(0)
                    .overrideTexturing("block/steam")
                    .passThroughPredicate(state -> state.isAir() || state.is(DISTILLED_WATER.FLUID_BLOCK.get()))
                    .register())
            .Register(p -> p.density(-1));


    public static void register(){
    }


}
