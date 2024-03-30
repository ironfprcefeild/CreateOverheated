package net.ironf.overheated;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.laserOptics.Diode.DiodeBlock;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlock;
import net.ironf.overheated.laserOptics.mirrors.mirrorBlock;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlock;
import net.ironf.overheated.laserOptics.thermometer.ThermometerBlock;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserBlock;
import net.ironf.overheated.steamworks.blocks.heatsink.HeatSinkBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.item.ChamberItemBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.steam.ChamberSteamBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlock;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlocks {


    static {
        Overheated.REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.OVERHEATED_TAB);
    }
    ////Steam Works
    //Steam Vent
    public static final BlockEntry<steamVentBlock> STEAM_VENT = REGISTRATE
            .block("steam_vent", steamVentBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Turbine
    public static final BlockEntry<Block> TURBINE_EXTENSION = REGISTRATE
            .block("turbine_extension", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> TURBINE_CENTER = REGISTRATE
            .block("turbine_center", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    public static final BlockEntry<turbineEndBlock> TURBINE_END = REGISTRATE
            .block("turbine_end", turbineEndBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    //Condenser
    public static final BlockEntry<CondenserBlock> CONDENSER = REGISTRATE
            .block("condenser", CondenserBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();


    //// Lazer Optics

    //Blaze Crucible

    public static final BlockEntry<BlazeCrucibleBlock> BLAZE_CRUCIBLE = REGISTRATE
            .block("blaze_crucible", BlazeCrucibleBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Diode
    public static final BlockEntry<DiodeBlock> DIODE = REGISTRATE
            .block("diode", DiodeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Anti-Laser Plating
    public static final BlockEntry<Block> ANTI_LASER_PLATING = REGISTRATE
            .block("anti_laser_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Mirrors
    public static final BlockEntry<mirrorBlock> BASIC_MIRROR = REGISTRATE
            .block("basic_mirror", mirrorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Dimmers
    public static final BlockEntry<Block> SUPERHEAT_DIMMER = REGISTRATE
            .block("superheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> OVERHEAT_DIMMER = REGISTRATE
            .block("overheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    //Thermometer
    public static final BlockEntry<ThermometerBlock> THERMOMETER = REGISTRATE
            .block("thermometer", ThermometerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    //Solar Panel
    public static final BlockEntry<SolarPanelBlock> SOLAR_PANEL = REGISTRATE
            .block("solar_panel", SolarPanelBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Heat Sink
    public static final BlockEntry<HeatSinkBlock> HEAT_SINK = REGISTRATE
            .block("heat_sink", HeatSinkBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL).noOcclusion())
            .simpleItem()
            .register();

    ////Pressure Chamber

    public static final BlockEntry<ChamberCoreBlock> CHAMBER_CORE = REGISTRATE
            .block("pressure_chamber_core", ChamberCoreBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    public static final BlockEntry<ChamberSteamBlock> CHAMBER_STEAM = REGISTRATE
            .block("pressure_chamber_steam", ChamberSteamBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    public static final BlockEntry<ChamberItemBlock> CHAMBER_ITEM = REGISTRATE
            .block("pressure_chamber_item", ChamberItemBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    public static void register(){

    }

}
