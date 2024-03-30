package net.ironf.overheated.gasses;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllGasses {

    //Will be used later, testing for now
    public static final FluidEntry<ForgeFlowingFluid.Flowing> morkite = REGISTRATE.gas("morkite",GasFluidSource::new)
            .register(REGISTRATE.gasBlock("morkite")
                    .defaultFlow(Direction.DOWN)
                    .shiftChance(2)
                    .tickDelays(5,8)
                    .register());

    public static void register(){
    }


}
