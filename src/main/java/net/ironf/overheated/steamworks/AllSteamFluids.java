package net.ironf.overheated.steamworks;

import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.GasFluidSource;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Arrays;
import java.util.function.Predicate;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllSteamFluids {



    //I declare creative tabs twice so that distilled water doesn't end up in the steam buckets tab
    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }


    public static final OverheatedRegistrate.FluidRegistration DISTILLED_WATER =
            REGISTRATE.SimpleFluid("distilled_water")
                    .tintColor(0x33B3FF)
                    .levelDecreasePerBlock(2)
                    .tickRate(20)
                    .explosionResistance(10f)
                    .slopeFindDistance(6)
                    .Register(p -> p.canHydrate(false)
                            .canDrown(true)
                            .canSwim(true)
                            .canExtinguish(true));


    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_STEAM_BUCKETS_TAB);
    }


    public static OverheatedRegistrate.FluidRegistration registerSteam(int PressureLevel, int HeatRating){
        String name = heatingIDs[HeatRating] + "steam_" + pressureIDs[PressureLevel - 1];
        return REGISTRATE.SimpleFluid(name)
                .tintColor(0x33B3FF)
                .overrideTexture("block/fluids/steam")
                .addBucketToSteamTabOnly()
                .bucketModelLocation("steam_bucket")
                .setGas(REGISTRATE.gasBlock(name)
                        .shiftChance(4)
                        .tickDelays(2,8 - PressureLevel)
                        .defaultFlow(Direction.UP)
                        .explosionSafety(9 - (PressureLevel+HeatRating))
                        .overideTexturing("block/steam")
                        .register())
                .Register(p -> p.supportsBoating(false).viscosity(0).density(-1));
    }

    public static final String[] pressureIDs = {"low","mid","high","insane"};
    public static final String[] heatingIDs = {"","heated_","superheated_","overheated_"};
    public static final OverheatedRegistrate.FluidRegistration STEAM_LOW = registerSteam(1,0);
    public static final OverheatedRegistrate.FluidRegistration STEAM_MID = registerSteam(2,0);
    public static final OverheatedRegistrate.FluidRegistration STEAM_HIGH = registerSteam(3,0);
    public static final OverheatedRegistrate.FluidRegistration STEAM_INSANE = registerSteam(4,0);
    public static final OverheatedRegistrate.FluidRegistration HEATED_STEAM_LOW = registerSteam(1,1);
    public static final OverheatedRegistrate.FluidRegistration HEATED_STEAM_MID = registerSteam(2,1);
    public static final OverheatedRegistrate.FluidRegistration HEATED_STEAM_HIGH = registerSteam(3,1);
    public static final OverheatedRegistrate.FluidRegistration HEATED_STEAM_INSANE = registerSteam(4,1);
    public static final OverheatedRegistrate.FluidRegistration SUPERHEATED_STEAM_LOW = registerSteam(1,2);
    public static final OverheatedRegistrate.FluidRegistration SUPERHEATED_STEAM_MID = registerSteam(2,2);
    public static final OverheatedRegistrate.FluidRegistration SUPERHEATED_STEAM_HIGH = registerSteam(3,2);
    public static final OverheatedRegistrate.FluidRegistration SUPERHEATED_STEAM_INSANE = registerSteam(4,2);
    public static final OverheatedRegistrate.FluidRegistration OVERHEATED_STEAM_LOW = registerSteam(1,3);
    public static final OverheatedRegistrate.FluidRegistration OVERHEATED_STEAM_MID = registerSteam(2,3);
    public static final OverheatedRegistrate.FluidRegistration OVERHEATED_STEAM_HIGH = registerSteam(3,3);
    public static final OverheatedRegistrate.FluidRegistration OVERHEATED_STEAM_INSANE = registerSteam(4,3);


    //An array containing all steams, first sorted by pressure (0-4) then by heat (0-3)
    public static @NonnullType Fluid[][] Steams;

    public static void prepareSteamArray(){
        Overheated.LOGGER.info("Preparing Steam Utility Array");
        Steams = new Fluid[][]{
                {DISTILLED_WATER.SOURCE.get(), DISTILLED_WATER.SOURCE.get(), DISTILLED_WATER.SOURCE.get(), DISTILLED_WATER.SOURCE.get()},
                {STEAM_LOW.SOURCE.get().getSource(), HEATED_STEAM_LOW.SOURCE.get().getSource(), SUPERHEATED_STEAM_LOW.SOURCE.get().getSource(), OVERHEATED_STEAM_LOW.SOURCE.get().getSource()},
                {STEAM_MID.SOURCE.get().getSource(),HEATED_STEAM_MID.SOURCE.get().getSource(),SUPERHEATED_STEAM_MID.SOURCE.get().getSource(),OVERHEATED_STEAM_MID.SOURCE.get().getSource()},
                {STEAM_HIGH.SOURCE.get().getSource(),HEATED_STEAM_HIGH.SOURCE.get().getSource(),SUPERHEATED_STEAM_HIGH.SOURCE.get().getSource(),OVERHEATED_STEAM_HIGH.SOURCE.get().getSource()},
                {STEAM_INSANE.SOURCE.get().getSource(),HEATED_STEAM_INSANE.SOURCE.get().getSource(),SUPERHEATED_STEAM_INSANE.SOURCE.get().getSource(),OVERHEATED_STEAM_INSANE.SOURCE.get().getSource()}
        };
    }



    public static void register(){

    }



    //Helper Functions

    public static FluidStack getSteamFromValues(int p, int h, int stackSize){
        return new FluidStack(Steams[Math.min(Math.max(p,0),4)][Math.min(Math.max(h,0),3)],stackSize);
    }

    public static int getSteamPressure(Fluid s){
        int p = 0;
        for (Fluid[] pressureLevel : Steams){
            if (Arrays.stream(pressureLevel).anyMatch(Predicate.isEqual(s))){
                return p;
            }
            p++;
        }
        return 0;
    }

    public static int getSteamHeat(Fluid s){
        int h = 0;
        for (Fluid[] steamLevel : Steams) {
            for (Fluid steam : steamLevel) {
                if (steam == s) {
                    return h;
                }
                h++;
            }
            h = 0;
        }
        return 0;
    }
    public static int getSteamPressure(FluidStack s){
        Fluid fluid = s.getFluid();
        if (fluid instanceof EmptyFluid){
            return 0;
        }
        return getSteamPressure(fluid);
    }

    public static int getSteamHeat(FluidStack s){
        Fluid fluid = s.getFluid();
        if (fluid instanceof EmptyFluid){
            return 0;
        }
        return getSteamHeat(fluid);
    }

}
