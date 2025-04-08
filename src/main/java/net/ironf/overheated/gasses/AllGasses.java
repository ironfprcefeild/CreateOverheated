package net.ironf.overheated.gasses;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllGasses {
    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }
    //TODO make proper texture for morkite and gasses instead of the placeholder in the resources right now
    // (Morkite is using steam bucket as placeholder)
    //Will be used later, testing for now
    public static final FluidEntry<ForgeFlowingFluid.Flowing> morkite = REGISTRATE.gas("morkite",GasFluidSource::new)
            .Density(2)
            .overrideTexturing("steam")
            .register(REGISTRATE.gasBlock("morkite")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(5,8)
                    .explosionSafety(0)
                    .register());

    public static final FluidEntry<ForgeFlowingFluid.Flowing> nihilite_gas = REGISTRATE.gas("nihilite_gas",GasFluidSource::new)
            .Density(8)
            .overrideTexturing("steam")
            .register(REGISTRATE.gasBlock("nihilite_gas")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(2,8)
                    .explosionSafety(10)
                    .register());

    public static final FluidEntry<ForgeFlowingFluid.Flowing> ammonia = REGISTRATE.gas("ammonia",GasFluidSource::new)
            .Density(-3)
            .overrideTexturing("steam")
            .register(REGISTRATE.gasBlock("ammonia")
                    .defaultFlow(Direction.UP)
                    .shiftChance(5)
                    .tickDelays(2,5)
                    .explosionSafety(12)
                    .register());

    public static final FluidEntry<ForgeFlowingFluid.Flowing> water_vapor = REGISTRATE.gas("water_vapor",GasFluidSource::new)
            .Density(-3)
            .overrideTexturing("steam")
            .register(REGISTRATE.gasBlock("water_vapor")
                    .defaultFlow(Direction.UP)
                    .shiftChance(2)
                    .tickDelays(2,3)
                    .explosionSafety(0)
                    .register());

    public static void register(){
    }


}
