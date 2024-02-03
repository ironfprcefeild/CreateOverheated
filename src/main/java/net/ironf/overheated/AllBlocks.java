package net.ironf.overheated;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.laserOptics.backend.beam.BeamBlock;
import net.ironf.overheated.laserOptics.backend.beam.laserCyst.LaserCystBlock;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlock;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineCenter.turbineCenterBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlocks {


    static {
        Overheated.REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.OVERHEATED_TAB);
    }
    ////Steam Works
    public static final BlockEntry<steamVentBlock> STEAM_VENT = REGISTRATE
            .block("steam_vent", steamVentBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Turbine
    public static final BlockEntry<Block> TURBINE_EXTENSION = REGISTRATE
            .block("turbine_extension", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    public static final BlockEntry<turbineCenterBlock> TURBINE_CENTER = REGISTRATE
            .block("turbine_center", turbineCenterBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    public static final BlockEntry<turbineEndBlock> TURBINE_END = REGISTRATE
            .block("turbine_end", turbineEndBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //// Lazer Optics

    //Beam
    public static final BlockEntry<BeamBlock> BEAM = REGISTRATE
            .block("laser_beam", BeamBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .register();

    //Cyst
    public static final BlockEntry<LaserCystBlock> LASER_CYST = REGISTRATE
            .block("laser_beam", LaserCystBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .register();
    //Blaze Crucible

    public static final BlockEntry<BlazeCrucibleBlock> BLAZE_CRUCIBLE = REGISTRATE
            .block("blaze_crucible", BlazeCrucibleBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    public static void register(){

    }

}
