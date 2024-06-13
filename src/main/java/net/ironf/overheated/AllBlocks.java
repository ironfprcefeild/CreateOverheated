package net.ironf.overheated;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.GasHood.GasHoodBlock;
import net.ironf.overheated.laserOptics.Diode.DiodeBlock;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlock;
import net.ironf.overheated.laserOptics.mirrors.mirrorBlock;
import net.ironf.overheated.laserOptics.mirrors.splitMirror.SplitMirrorBlock;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlock;
import net.ironf.overheated.laserOptics.thermometer.ThermometerBlock;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserBlock;
import net.ironf.overheated.steamworks.blocks.geothermals.GeothermalInterfaceBlock;
import net.ironf.overheated.steamworks.blocks.heatsink.HeatSinkBlock;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlock;
import net.ironf.overheated.steamworks.blocks.pressureHeater.PressureHeaterBlock;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlock;
import net.ironf.overheated.utility.data.GenericBlockStateGen;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlocks {


    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    //Generic Blocks
    public static final BlockEntry<Block> WHITE_SALT_CRYSTAL =
            REGISTRATE.block("white_salt_crystal", Block::new)
                    .initialProperties(() -> Blocks.AMETHYST_BLOCK)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("white_salt_crystal"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("White Salt Crystal Block")
                    .register();
    public static final BlockEntry<Block> WHITE_SALT_BLOCK =
            REGISTRATE.block("white_salt_block", Block::new)
                    .initialProperties(() -> Blocks.DEEPSLATE)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("white_salt_block"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("White Salt Block")
                    .register();



    ////Steam Works
    //Steam Vent
    //TODO Put in models on blocks that need them
    public static final BlockEntry<steamVentBlock> STEAM_VENT = REGISTRATE
            .block("steam_vent", steamVentBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Turbine
    public static final BlockEntry<Block> TURBINE_EXTENSION = REGISTRATE
            .block("turbine_extension", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("turbine_extension"))
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> TURBINE_CENTER = REGISTRATE
            .block("turbine_center", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("turbine_center"))
            .defaultLoot()
            .register();

    public static final BlockEntry<turbineEndBlock> TURBINE_END = REGISTRATE
            .block("turbine_end", turbineEndBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    //Condenser
    public static final BlockEntry<CondenserBlock> CONDENSER = REGISTRATE
            .block("condenser", CondenserBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    //Heat Sink
    public static final BlockEntry<HeatSinkBlock> HEAT_SINK = REGISTRATE
            .block("heat_sink", HeatSinkBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Pressure Chamber

    public static final BlockEntry<ChamberCoreBlock> CHAMBER_CORE = REGISTRATE
            .block("pressure_chamber_core", ChamberCoreBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("pressure_chamber_core"))
            .defaultLoot()
            .register();
    public static final BlockEntry<Block> CHAMBER_HEAT_SINK = REGISTRATE
            .block("pressure_chamber_heat_sink", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("pressure_chamber_heat_sink"))
            .defaultLoot()
            .register();



    //Impact Drill
    public static final BlockEntry<ImpactDrillBlock> IMPACT_DRILL = REGISTRATE
            .block("impact_drill", ImpactDrillBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)

            .defaultLoot()
            .register();

    public static final BlockEntry<Block> IMPACT_TUBING = REGISTRATE
            .block("impact_tubing", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("impact_tubing"))
            .defaultLoot()
            .register();

    //Gas Hood
    public static final BlockEntry<GasHoodBlock> GAS_HOOD = REGISTRATE
            .block("gas_hood", GasHoodBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Pressure Heater
    public static final BlockEntry<PressureHeaterBlock> PRESSURE_HEATER= REGISTRATE
            .block("pressure_heater", PressureHeaterBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //// Laser Optics

    //Blaze Crucible

    public static final BlockEntry<BlazeCrucibleBlock> BLAZE_CRUCIBLE = REGISTRATE
            .block("blaze_crucible", BlazeCrucibleBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Diode
    public static final BlockEntry<DiodeBlock> DIODE = REGISTRATE
            .block("diode", DiodeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Laser Film
    public static final BlockEntry<Block> LASER_FILM = REGISTRATE
            .block("laser_film", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("laser_film"))
            .defaultLoot()
            .register();


    //Anti-Laser Plating
    public static final BlockEntry<Block> ANTI_LASER_PLATING = REGISTRATE
            .block("anti_laser_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("anti_laser_plating"))
            .defaultLoot()
            .register();

    //Mirrors
    public static final BlockEntry<mirrorBlock> BASIC_MIRROR = REGISTRATE
            .block("basic_mirror", mirrorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    public static final BlockEntry<SplitMirrorBlock> SPLIT_MIRROR = REGISTRATE
            .block("split_mirror", SplitMirrorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Dimmers
    public static final BlockEntry<Block> SUPERHEAT_DIMMER = REGISTRATE
            .block("superheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("superheat_dimmer"))
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> OVERHEAT_DIMMER = REGISTRATE
            .block("overheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("overheat_dimmer"))
            .defaultLoot()
            .register();
    //Thermometer
    public static final BlockEntry<ThermometerBlock> THERMOMETER = REGISTRATE
            .block("thermometer", ThermometerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    //Solar Panel
    public static final BlockEntry<SolarPanelBlock> SOLAR_PANEL = REGISTRATE
            .block("solar_panel", SolarPanelBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)

            .register();

    //Geothermal Vents
    public static final BlockEntry<Block> HEATED_VENT = REGISTRATE.block("heated_geothermal_vent", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.strength(-1.0F, 3600000.0F).noLootTable())
            .blockstate(simpleCubeAll("superheated_geothermal_vent"))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> SUPERHEATED_VENT = REGISTRATE.block("superheated_geothermal_vent", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.strength(-1.0F, 3600000.0F).noLootTable())
            .blockstate(simpleCubeAll("superheated_geothermal_vent"))
            .simpleItem()
            .register();

    public static final BlockEntry<GeothermalInterfaceBlock> GEOTHERMAL_INTERFACE = REGISTRATE
            .block("geothermal_interface", GeothermalInterfaceBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)

            .register();

    /////Everything Else

    //Salt Cave Blocks


    public static void register(){

    }

}
