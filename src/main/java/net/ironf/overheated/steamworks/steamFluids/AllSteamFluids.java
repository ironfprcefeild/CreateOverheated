package net.ironf.overheated.steamworks.steamFluids;

import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

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
    public static void register(){

    }

    //Helper Functions

    public static VirtualFluid getSteamFromPressure(int p){
        if (p <= 0){
            return DISTILLED_WATER.get();
        } else if (p == 1){
            return STEAM_LOW.get();
        } else if (p == 2){
            return STEAM_MID.get();
        } else if (p == 3){
            return STEAM_HIGH.get();
        } else {
            return STEAM_INSANE.get();
        }
    }

    public static int getSteamPressure(VirtualFluid s){
        if (s.isSame(STEAM_LOW.get())){
            return 1;
        } else if (s.isSame(STEAM_MID.get())) {
            return 2;
        } else if (s.isSame(STEAM_HIGH.get())){
            return 3;
        } else if (s.isSame(STEAM_INSANE.get())){
            return 4;
        } else {
            return 0;
        }
    }
    public static int getSteamPressure(FluidStack s){
        Fluid fluid = s.getFluid();
        if (fluid instanceof EmptyFluid){
            return 0;
        }
        return getSteamPressure((VirtualFluid) fluid);
    }

}
