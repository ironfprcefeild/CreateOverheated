package net.ironf.overheated.gasses;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.core.Direction;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllGasses {
    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }
    //TODO make proper texture for morkite and gasses instead of the placeholder in the resources right now
    // (Morkite is using steam bucket as placeholder)
    //TODO Also make actual tint colors

    public static final OverheatedRegistrate.FluidRegistration morkite = REGISTRATE.SimpleFluid("morkite")
            .tintColor(0x33B3FF)
            .setGas(REGISTRATE.gasBlock("morkite")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(5,8)
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(8));

    public static final OverheatedRegistrate.FluidRegistration nihilite_gas = REGISTRATE.SimpleFluid("nihilite_gas")
            .tintColor(0x33B3FF)
            .setGas(REGISTRATE.gasBlock("nihilite_gas")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(2,8)
                    .explosionSafety(10)
                    .register())
            .Register(p -> p.density(8));

    public static final OverheatedRegistrate.FluidRegistration ammonia = REGISTRATE.SimpleFluid("ammonia")
            .tintColor(0x33B3FF)
            .overrideTexture("steam")
            .setGas(REGISTRATE.gasBlock("ammonia")
                    .defaultFlow(Direction.UP)
                    .shiftChance(5)
                    .tickDelays(2,5)
                    .explosionSafety(12)
                    .register())
            .Register(p -> p.density(-3));

    public static final OverheatedRegistrate.FluidRegistration water_vapor = REGISTRATE.SimpleFluid("water_vapor")
            .tintColor(0x33B3FF)
            .overrideTexture("steam")
            .makeGasUnCapturable()
            .setGas(REGISTRATE.gasBlock("water_vapor")
                    .defaultFlow(Direction.UP)
                    .shiftChance(0)
                    .tickDelays(2,3)
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(-1));

    public static final OverheatedRegistrate.FluidRegistration oxygen = REGISTRATE.SimpleFluid("oxygen")
            .tintColor(0x33B3FF)
            .overrideTexture("steam")
            .makeGasUnCapturable()
            .setGas(REGISTRATE.gasBlock("oxygen")
                    .defaultFlow(Direction.UP)
                    .shiftChance(3)
                    .tickDelays(1,5)
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(-1));

    public static final OverheatedRegistrate.FluidRegistration hydrogen = REGISTRATE.SimpleFluid("hydrogen")
            .tintColor(0x33B3FF)
            .overrideTexture("steam")
            .makeGasUnCapturable()
            .setGas(REGISTRATE.gasBlock("hydrogen")
                    .defaultFlow(Direction.UP)
                    .shiftChance(5)
                    .tickDelays(1,3)
                    .explosionSafety(0)
                    .register())
            .Register(p -> p.density(-1));


    public static void register(){
    }


}
