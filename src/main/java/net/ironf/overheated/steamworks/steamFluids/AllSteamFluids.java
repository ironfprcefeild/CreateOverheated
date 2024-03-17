package net.ironf.overheated.steamworks.steamFluids;

import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.steamworks.steamFluids.inWorldSteam.SteamFluidSource;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Arrays;
import java.util.function.Predicate;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllSteamFluids {



//TODO give the right fluid properties to distilled water

    static {
        Overheated.REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.OVERHEATED_TAB);
    }

    public static final FluidEntry<ForgeFlowingFluid.Flowing> DISTILLED_WATER =
            REGISTRATE.standardFluid("distilled_water")
                    .lang("Distilled Water")
                    .properties(b -> b.density(1))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .slopeFindDistance(3)
                            .explosionResistance(100f)
                            )
                    .source(ForgeFlowingFluid.Source::new) // TODO: remove when Registrate fixes FluidBuilder
                    .bucket()
                    .build()
                    .register();

    public static FluidEntry<ForgeFlowingFluid.Flowing> registerSteam(int PressureLevel, int HeatRating){
        SteamFluidSource.nextPressureLevel = PressureLevel;
        SteamFluidSource.nextHeatRating = HeatRating;
        return REGISTRATE.standardFluid(heatingIDs[HeatRating] + "steam_" + pressureIDs[PressureLevel - 1])
                .properties(b -> b.supportsBoating(false).viscosity(0))
                .fluidProperties(p -> p.levelDecreasePerBlock(10).slopeFindDistance(1).tickRate(1))
                .source(SteamFluidSource::new)
                .noBucket()
                .register();

    }

    public static final String[] pressureIDs = {"low","mid","high","insane"};
    public static final String[] heatingIDs = {"","heated_","superheated_","overheated_"};
    public static final FluidEntry<ForgeFlowingFluid.Flowing> STEAM_LOW = registerSteam(1,0);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> STEAM_MID = registerSteam(2,0);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> STEAM_HIGH = registerSteam(3,0);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> STEAM_INSANE = registerSteam(4,0);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> HEATED_STEAM_LOW = registerSteam(1,1);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> HEATED_STEAM_MID = registerSteam(2,1);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> HEATED_STEAM_HIGH = registerSteam(3,1);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> HEATED_STEAM_INSANE = registerSteam(4,1);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SUPERHEATED_STEAM_LOW = registerSteam(1,2);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SUPERHEATED_STEAM_MID = registerSteam(2,2);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SUPERHEATED_STEAM_HIGH = registerSteam(3,2);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SUPERHEATED_STEAM_INSANE = registerSteam(4,2);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> OVERHEATED_STEAM_LOW = registerSteam(1,3);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> OVERHEATED_STEAM_MID = registerSteam(2,3);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> OVERHEATED_STEAM_HIGH = registerSteam(3,3);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> OVERHEATED_STEAM_INSANE = registerSteam(4,3);


    //An array containing all steams, first sorted by pressure (0-4) then by heat (0-3)
    public static @NonnullType Fluid[][] Steams;

    public static void prepareSteamArray(){
        Steams = new Fluid[][]{
                {DISTILLED_WATER.get(), DISTILLED_WATER.get(), DISTILLED_WATER.get(), DISTILLED_WATER.get()},
                {STEAM_LOW.get(), HEATED_STEAM_LOW.get(), SUPERHEATED_STEAM_LOW.get(), OVERHEATED_STEAM_LOW.get()},
                {STEAM_MID.get(),HEATED_STEAM_MID.get(),SUPERHEATED_STEAM_MID.get(),OVERHEATED_STEAM_MID.get()},
                {STEAM_HIGH.get(),HEATED_STEAM_HIGH.get(),SUPERHEATED_STEAM_HIGH.get(),OVERHEATED_STEAM_HIGH.get()},
                {STEAM_INSANE.get(),HEATED_STEAM_INSANE.get(),SUPERHEATED_STEAM_INSANE.get(),OVERHEATED_STEAM_INSANE.get()}
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
        return getSteamHeat(s,getSteamPressure(s));
    }
    public static int getSteamHeat(Fluid s, int pressure){
        int h = 0;
        for (Fluid heatLevel : Steams[pressure]){
            if (heatLevel.isSame(s)){
                return h;
            }
            h++;
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
