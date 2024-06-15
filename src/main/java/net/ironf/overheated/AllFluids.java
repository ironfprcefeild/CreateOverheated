package net.ironf.overheated;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllFluids {
    public static void register(){
        AllSteamFluids.register();
        AllGasses.register();
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BLAZE_NECTAR =
            REGISTRATE.standardFluid("blaze_nectar", OverheatedRegistrate.SolidRenderedPlaceableFluidType.create(
                            0xE99F19,
                            () -> 1f / 8f * 2f))
                    .lang("Blaze Nectar")
                    .source(ForgeFlowingFluid.Source::new)
                    .bucket()
                    .build()
                    .register();


}
