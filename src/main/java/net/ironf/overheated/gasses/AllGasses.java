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
                    .register());

    public static void register(){
    }


}
