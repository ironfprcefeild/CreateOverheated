package net.ironf.overheated;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.foundation.data.SharedProperties;
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
import net.ironf.overheated.utility.data.GenericDirectionalBlockStateGen;
import net.ironf.overheated.utility.data.GenericSpunDirectionalBlockStateGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
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
                    .lang("Block of White Salt Crystals")
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
                    .lang("Block of White Salt")
                    .register();

    public static final BlockEntry<Block> RED_SALT_CRYSTAL =
            REGISTRATE.block("red_salt_crystal", Block::new)
                    .initialProperties(() -> Blocks.AMETHYST_BLOCK)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("red_salt_crystal"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Red Salt Crystals")
                    .register();
    public static final BlockEntry<Block> RED_SALT_BLOCK =
            REGISTRATE.block("red_salt_block", Block::new)
                    .initialProperties(() -> Blocks.DEEPSLATE)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("red_salt_block"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Red Salt")
                    .register();
    public static final BlockEntry<Block> BLUE_SALT_CRYSTAL =
            REGISTRATE.block("blue_salt_crystal", Block::new)
                    .initialProperties(() -> Blocks.AMETHYST_BLOCK)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("blue_salt_crystal"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Blue Salt Crystals")
                    .register();
    public static final BlockEntry<Block> BLUE_SALT_BLOCK =
            REGISTRATE.block("blue_salt_block", Block::new)
                    .initialProperties(() -> Blocks.DEEPSLATE)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("blue_salt_block"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Blue Salt")
                    .register();
    public static final BlockEntry<Block> REINFORCED_INDUSTRIAL_IRON =
            REGISTRATE.block("reinforced_industrial_iron", Block::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops())
                    .transform(pickaxeOnly())
                    .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Reinforced Industrial Iron")
                    .register();
    public static final BlockEntry<Block> STURDY_SHEET_BLOCK =
            REGISTRATE.block("sturdy_sheet_block", Block::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops())
                    .transform(pickaxeOnly())
                    .simpleItem()
                    .defaultLoot()
                    .lang("Sturdy Sheet Block")
                    .register();
    public static final BlockEntry<Block> BLAZESTEEL_BLOCK =
            REGISTRATE.block("blazesteel_block", Block::new)
                    .initialProperties(() -> Blocks.IRON_BLOCK)
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.GILDED_BLACKSTONE))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("blazesteel_block"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block Blazesteel")
                    .register();
    public static final BlockEntry<Block> BLAZEGLASS  =
            REGISTRATE.block("blazeglass", Block::new)
                    .initialProperties(() -> Blocks.GLASS)
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE))
                    .blockstate(simpleCubeAll("blazeglass"))
                    .simpleItem()
                    .transform(pickaxeOnly())
                    .defaultLoot()
                    .lang("Blazeglass")
                    .register();

    //TODO Blazeglass pane blockstate
    public static final BlockEntry<IronBarsBlock> BLAZEGLASS_PANE  =
            REGISTRATE.block("blazeglass_pane", IronBarsBlock::new)
                    .initialProperties(() -> Blocks.GLASS_PANE)
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE))
                    .simpleItem()
                    .transform(pickaxeOnly())
                    .defaultLoot()
                    .lang("Blazeglass Pane")
                    .register();

    //TODO figure out how casings work, and add sprite shift stuff
    public static final BlockEntry<CasingBlock> LASER_CASING = REGISTRATE.block("laser_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            //.transform(BuilderTransformers.casing(() -> AllSpriteShifts.ANDESITE_CASING))
            .register();

    ////Steam Works
    //Steam Vent
    //TODO Put in models on blocks that need them, remove placeholders
    // Theese also include the weird stuff in place of a .simple item
    public static final BlockEntry<steamVentBlock> STEAM_VENT = REGISTRATE
            .block("steam_vent", steamVentBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Turbine
    public static final BlockEntry<Block> TURBINE_EXTENSION = REGISTRATE
            .block("turbine_extension", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> TURBINE_CENTER = REGISTRATE
            .block("turbine_center", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();

    public static final BlockEntry<turbineEndBlock> TURBINE_END = REGISTRATE
            .block("turbine_end", turbineEndBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    //Condenser
    public static final BlockEntry<CondenserBlock> CONDENSER = REGISTRATE
            .block("condenser", CondenserBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
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
            .blockstate(new GenericSpunDirectionalBlockStateGen((ctx, prov, state) -> "block/heat_sink")::generate)
            .defaultLoot()
            .register();

    //Pressure Chamber

    public static final BlockEntry<ChamberCoreBlock> CHAMBER_CORE = REGISTRATE
            .block("pressure_chamber_core", ChamberCoreBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();
    public static final BlockEntry<Block> CHAMBER_HEAT_SINK = REGISTRATE
            .block("pressure_chamber_heat_sink", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();



    //Impact Drill
    public static final BlockEntry<ImpactDrillBlock> IMPACT_DRILL = REGISTRATE
            .block("impact_drill", ImpactDrillBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)

            .defaultLoot()
            .register();

    public static final BlockEntry<Block> IMPACT_TUBING = REGISTRATE
            .block("impact_tubing", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();

    //Gas Hood
    public static final BlockEntry<GasHoodBlock> GAS_HOOD = REGISTRATE
            .block("gas_hood", GasHoodBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new GenericDirectionalBlockStateGen((ctx, prov, state) -> "block/gas_hood")::generate)
            .defaultLoot()
            .register();

    //Pressure Heater
    public static final BlockEntry<PressureHeaterBlock> PRESSURE_HEATER= REGISTRATE
            .block("pressure_heater", PressureHeaterBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
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
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Diode
    public static final BlockEntry<DiodeBlock> DIODE = REGISTRATE
            .block("diode", DiodeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Laser Film
    public static final BlockEntry<Block> LASER_FILM = REGISTRATE
            .block("laser_film", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();


    //Anti-Laser Plating
    public static final BlockEntry<Block> ANTI_LASER_PLATING = REGISTRATE
            .block("antilaser_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("antilaser_plating"))
            .defaultLoot()
            .register();

    //Mirrors
    public static final BlockEntry<mirrorBlock> BASIC_MIRROR = REGISTRATE
            .block("basic_mirror", mirrorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    public static final BlockEntry<SplitMirrorBlock> SPLIT_MIRROR = REGISTRATE
            .block("split_mirror", SplitMirrorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Dimmers
    public static final BlockEntry<Block> SUPERHEAT_DIMMER = REGISTRATE
            .block("superheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> OVERHEAT_DIMMER = REGISTRATE
            .block("overheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();
    //Thermometer
    public static final BlockEntry<ThermometerBlock> THERMOMETER = REGISTRATE
            .block("thermometer", ThermometerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    //Solar Panel
    public static final BlockEntry<SolarPanelBlock> SOLAR_PANEL = REGISTRATE
            .block("solar_panel", SolarPanelBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)
            .register();

    //Geothermal Vents
    public static final BlockEntry<Block> HEATED_VENT = REGISTRATE.block("heated_geothermal_vent", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.strength(-1.0F, 3600000.0F).noLootTable())
            .blockstate(simpleCubeAll("placeholder"))
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .register();

    public static final BlockEntry<Block> SUPERHEATED_VENT = REGISTRATE.block("superheated_geothermal_vent", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.strength(-1.0F, 3600000.0F).noLootTable())
            .blockstate(simpleCubeAll("placeholder"))
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .register();

    public static final BlockEntry<GeothermalInterfaceBlock> GEOTHERMAL_INTERFACE = REGISTRATE
            .block("geothermal_interface", GeothermalInterfaceBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p)
            .item().model((ctx,prov) -> prov.getExistingFile(new ResourceLocation(Overheated.MODID,"placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new GenericBlockStateGen((ctx,prov,state) -> "block/place_holder")::generate)

            .register();

    /////Everything Else

    //Salt Cave Blocks


    public static void register(){

    }

}
