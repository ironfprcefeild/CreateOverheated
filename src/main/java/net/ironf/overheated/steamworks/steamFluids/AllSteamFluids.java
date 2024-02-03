package net.ironf.overheated.steamworks.steamFluids;

import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.simibubi.create.Create.REGISTRATE;

public class AllSteamFluids {

    public static final FluidEntry<VirtualFluid> DISTILLED_WATER = REGISTRATE.virtualFluid("distilled_water")
            .lang("Distilled Water")
            .register();
    public static final FluidEntry<VirtualFluid> STEAM_LOW = REGISTRATE.virtualFluid("steam_low")
            .lang("Low Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> STEAM_MID = REGISTRATE.virtualFluid("steam_mid")
            .lang("Medium Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> STEAM_HIGH = REGISTRATE.virtualFluid("steam_high")
            .lang("High Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> STEAM_INSANE = REGISTRATE.virtualFluid("steam_insane")
            .lang("Insane Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> HEATED_STEAM_LOW = REGISTRATE.virtualFluid("heated_steam_low")
            .lang("Heated Low Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> HEATED_STEAM_MID = REGISTRATE.virtualFluid("heated_steam_mid")
            .lang("Heated Medium Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> HEATED_STEAM_HIGH = REGISTRATE.virtualFluid("heated_steam_high")
            .lang("Heated High Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> HEATED_STEAM_INSANE = REGISTRATE.virtualFluid("heated_steam_insane")
            .lang("Heated Insane Pressure Steam")
            .register();


    public static final FluidEntry<VirtualFluid> SUPERHEATED_STEAM_LOW = REGISTRATE.virtualFluid("superheated_steam_low")
            .lang("Superheated Low Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> SUPERHEATED_STEAM_MID = REGISTRATE.virtualFluid("superheated_steam_mid")
            .lang("Superheated Medium Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> SUPERHEATED_STEAM_HIGH = REGISTRATE.virtualFluid("superheated_steam_high")
            .lang("Superheated High Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> SUPERHEATED_STEAM_INSANE = REGISTRATE.virtualFluid("superheated_steam_insane")
            .lang("Superheated Insane Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> OVERHEATED_STEAM_LOW = REGISTRATE.virtualFluid("overheated_steam_low")
            .lang("Overheated Low Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> OVERHEATED_STEAM_MID = REGISTRATE.virtualFluid("overheated_steam_mid")
            .lang("Overheated Medium Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> OVERHEATED_STEAM_HIGH = REGISTRATE.virtualFluid("overheated_steam_high")
            .lang("Overheated High Pressure Steam")
            .register();

    public static final FluidEntry<VirtualFluid> OVERHEATED_STEAM_INSANE = REGISTRATE.virtualFluid("overheated_steam_insane")
            .lang("Overheated Insane Pressure Steam")
            .register();

    //An array containing all steams, first sorted by pressure (0-4) then by heat (0-3)
    public static VirtualFluid[][] Steams;



    public static void register(){
        Steams = new VirtualFluid[][]{
                {DISTILLED_WATER.get(),DISTILLED_WATER.get(),DISTILLED_WATER.get(),DISTILLED_WATER.get()},
                {STEAM_LOW.get(), HEATED_STEAM_LOW.get(), SUPERHEATED_STEAM_LOW.get(), OVERHEATED_STEAM_LOW.get()},
                {STEAM_MID.get(),HEATED_STEAM_MID.get(),SUPERHEATED_STEAM_MID.get(),OVERHEATED_STEAM_MID.get()},
                {STEAM_HIGH.get(),HEATED_STEAM_HIGH.get(),SUPERHEATED_STEAM_HIGH.get(),OVERHEATED_STEAM_HIGH.get()},
                {STEAM_INSANE.get(),HEATED_STEAM_INSANE.get(),SUPERHEATED_STEAM_INSANE.get(),OVERHEATED_STEAM_INSANE.get()}
                };


    }

    //Helper Functions

    public static VirtualFluid getSteamFromValues(int p, int h){
        return Steams[Math.min(Math.max(p,0),4)][Math.min(Math.max(h,0),3)];
    }

    public static int getSteamPressure(VirtualFluid s){
        int p = 0;
        for (VirtualFluid[] pressureLevel : Steams){
            if (Arrays.stream(pressureLevel).anyMatch(Predicate.isEqual(s))){
                return p;
            }
            p++;
        }
        return 0;
    }

    public static int getSteamHeat(VirtualFluid s){
        return getSteamHeat(s,getSteamPressure(s));
    }
    public static int getSteamHeat(VirtualFluid s, int pressure){
        int h = 0;
        for (VirtualFluid heatLevel : Steams[pressure]){
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
        return getSteamPressure((VirtualFluid) fluid);
    }

    public static int getSteamHeat(FluidStack s){
        Fluid fluid = s.getFluid();
        if (fluid instanceof EmptyFluid){
            return 0;
        }
        return getSteamHeat((VirtualFluid) fluid);
    }

}
