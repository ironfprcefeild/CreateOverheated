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

    //Will be used later, testing for now
    public static final FluidEntry<ForgeFlowingFluid.Flowing> morkite = REGISTRATE.gas("morkite",GasFluidSource::new)
            .Density(2)
            .register(REGISTRATE.gasBlock("morkite")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(5,8)
                    .register());

    public static final FluidEntry<ForgeFlowingFluid.Flowing> lowDelay = REGISTRATE.gas("ld",GasFluidSource::new)
            .Density(2)
            .register(REGISTRATE.gasBlock("ld")
                    .defaultFlow(Direction.UP)
                    .shiftChance(1000)
                    .tickDelays(1,1)
                    .register());

    public static final FluidEntry<ForgeFlowingFluid.Flowing> highDelay = REGISTRATE.gas("hd",GasFluidSource::new)
            .Density(2)
            .register(REGISTRATE.gasBlock("hd")
                    .defaultFlow(Direction.SOUTH)
                    .shiftChance(1000)
                    .tickDelays(1,1)
                    .register());
    public static void register(){
    }


}
