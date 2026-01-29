package net.ironf.overheated;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.ironf.overheated.cooling.chillChannel.ChannelBlock;
import net.ironf.overheated.cooling.chillChannel.core.ChannelCoreBlock;
import net.ironf.overheated.cooling.chillChannel.expeller.ChannelExpellerBlock;
import net.ironf.overheated.cooling.cooler.CoolerBlock;
import net.ironf.overheated.cooling.coolingTower.CoolingTowerBlock;
import net.ironf.overheated.cooling.heatsink.HeatSinkBlock;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.GasHood.GasHoodBlock;
import net.ironf.overheated.laserOptics.Diode.DiodeBlock;
import net.ironf.overheated.laserOptics.DiodeJunction.DiodeJunctionBlock;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlock;
import net.ironf.overheated.laserOptics.mirrors.mirrorBlock;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlock;
import net.ironf.overheated.laserOptics.solarPanel.blazeAbsorber.BlazeAbsorberBlock;
import net.ironf.overheated.laserOptics.thermometer.ThermometerBlock;
import net.ironf.overheated.nuclear.radiolyzer.RadiolyzerBlock;
import net.ironf.overheated.nuclear.rods.control.ControlRodBlock;
import net.ironf.overheated.nuclear.rods.fuel.FuelRodBlock;
import net.ironf.overheated.steamworks.blocks.blowingEngine.BlowingEngineBlock;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserBlock;
import net.ironf.overheated.steamworks.blocks.geothermals.GeothermalInterfaceBlock;
import net.ironf.overheated.steamworks.blocks.geothermals.GeothermalVentBlock;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillBlock;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlock;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.ItemDuct.ItemDuctBlock;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.fluidDuct.FluidDuctBlock;
import net.ironf.overheated.steamworks.blocks.meterExtender.MeterExtenderBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionVentBlock;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlock;
import net.ironf.overheated.steamworks.blocks.pressureHeater.PressureHeaterBlock;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlock;
import net.ironf.overheated.utility.data.SimpleBlockStateGenerators;
import net.ironf.overheated.utility.data.blockstateModelGenerators.ModelBlockStateGen;
import net.ironf.overheated.utility.data.blockstateModelGenerators.ModelDirectionalBlockStateGen;
import net.ironf.overheated.utility.data.blockstateModelGenerators.ModelHorizontalDirectionalBlockStateGen;
import net.ironf.overheated.utility.data.blockstateModelGenerators.ModelSpunBlockStateGen;
import net.ironf.overheated.utility.registration.AllSpriteShifts;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.ironf.overheated.Overheated.REGISTRATE;
import static net.ironf.overheated.utility.registration.OverheatedRegistrate.easyConnectedTextures;

public class AllBlocks {


    static {
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    //TODO everything is indestructible lmaooo

    //Generic Blocks
    public static final BlockEntry<Block> WHITE_SALT_CRYSTAL =
            REGISTRATE.block("white_salt_crystal", Block::new)
                    .initialProperties(() -> Blocks.AMETHYST_BLOCK)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                            .requiresCorrectToolForDrops()
                            .lightLevel((state) -> 2)
                            .sound(SoundType.AMETHYST)
                            .strength(1f))
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
                            .sound(SoundType.DEEPSLATE)
                            .strength(2f))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("white_salt_block"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of White Salt")
                    .register();

    public static final BlockEntry<Block> RED_SALT_CRYSTAL =
            REGISTRATE.block("red_salt_crystal", Block::new)
                    .initialProperties(() -> Blocks.AMETHYST_BLOCK)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_RED)
                            .lightLevel((state) -> 2)
                            .requiresCorrectToolForDrops()
                            .strength(1f)
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
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_RED)
                            .requiresCorrectToolForDrops()
                            .strength(2f)
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
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                            .lightLevel((state) -> 2)
                            .requiresCorrectToolForDrops()
                            .strength(1f)
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
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                            .requiresCorrectToolForDrops()
                            .strength(2f)
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
                            .requiresCorrectToolForDrops()
                            .strength(2.5f))
                    .transform(pickaxeOnly())
                    .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Industrial Sheets")
                    .register();
    public static final BlockEntry<Block> STURDY_SHEET_BLOCK =
            REGISTRATE.block("sturdy_sheet_block", Block::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
                            .strength(4f))
                    .transform(pickaxeOnly())
                    .simpleItem()
                    .defaultLoot()
                    .lang("Sturdy Sheet Block")
                    .register();

    public static final BlockEntry<Block> REDSTONITE_BLOCK =
            REGISTRATE.block("redstonite_block", Block::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_RED)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()
                            .strength(4f))
                    .transform(pickaxeOnly())
                    .simpleItem()
                    .defaultLoot()
                    .lang("Redstonite Block")
                    .register();

    //TODO make it render properly
    public static final BlockEntry<GlassBlock> BLAZEGLASS  =
            REGISTRATE.block("blazeglass", GlassBlock::new)
                    .initialProperties(() -> Blocks.GLASS)
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE).strength(1.5f))
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
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE).strength(1f))
                    .simpleItem()
                    .transform(pickaxeOnly())
                    .defaultLoot()
                    .lang("Blazeglass Pane")
                    .register();
    /// Casings
    public static final BlockEntry<CasingBlock> LASER_CASING = REGISTRATE.block("laser_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_RED).sound(SoundType.NETHERITE_BLOCK).strength(3f))
            .transform(BuilderTransformers.casing(() -> AllSpriteShifts.LASER_CASING))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .register();
    public static final BlockEntry<CasingBlock> PRESSURIZED_CASING = REGISTRATE.block("pressurized_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(3f))
            .transform(BuilderTransformers.casing(() -> AllSpriteShifts.PRESSURIZED_CASING))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .register();
    public static final BlockEntry<CasingBlock> NUCLEAR_CASING = REGISTRATE.block("nuclear_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).strength(3f))
            .transform(BuilderTransformers.casing(() -> AllSpriteShifts.NUCLEAR_CASING))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .register();


    ///Nihilte Stuff
    public static final BlockEntry<Block> NIHILITE_BLOCK =
            REGISTRATE.block("nihilite_block", Block::new)
                    .initialProperties(() -> Blocks.DIAMOND_BLOCK)
                    .properties(p -> p.mapColor(MapColor.COLOR_CYAN)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST_CLUSTER)
                            .strength(5f))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("nihilite_block"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Block of Nihilite")
                    .register();


    public static final BlockEntry<Block> NIHILISTONE =
            REGISTRATE.block("nihilistone", Block::new)
                    .initialProperties(() -> Blocks.DIAMOND_BLOCK)
                    .properties(p -> p.mapColor(MapColor.COLOR_CYAN)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST_CLUSTER)
                            .strength(3f))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("nihilistone"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Nihilistone")
                    .register();

    public static final BlockEntry<Block> NIHBOROCK =
            REGISTRATE.block("nihborock", Block::new)
                    .initialProperties(() -> Blocks.DIAMOND_BLOCK)
                    .properties(p -> p.mapColor(MapColor.COLOR_CYAN)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.ANCIENT_DEBRIS)
                            .strength(2.5f))
                    .transform(pickaxeOnly())
                    .blockstate(SimpleBlockStateGenerators.simpleCubeColumn("nihborock"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Nihborock")
                    .register();

    ///Geothermium
    public static final BlockEntry<Block> GEOTHERMIUM =
            REGISTRATE.block("geothermium", Block::new)
                    .initialProperties(() -> Blocks.DEEPSLATE)
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DRIPSTONE_BLOCK)
                            .strength(3f))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("geothermium"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Geothermium")
                    .register();

    public static final BlockEntry<Block> NETHER_GEOTHERMIUM =
            REGISTRATE.block("nether_geothermium", Block::new)
                    .initialProperties(() -> Blocks.NETHER_GOLD_ORE)
                    .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE)
                            .strength(4f))
                    .transform(pickaxeOnly())
                    .blockstate(simpleCubeAll("nether_geothermium"))
                    .simpleItem()
                    .defaultLoot()
                    .lang("Nether Geothermium")
                    .register();
    ///Deposits
    //Geothermal Vents and deposits
    public static final BlockEntry<GeothermalVentBlock> HEATED_VENT = REGISTRATE.block("heated_geothermal_vent", GeothermalVentBlock::new)
            .onRegister(easyConnectedTextures(AllSpriteShifts.HEATED_GEOTHERMAL_VENT))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .lightLevel(s -> 8)
                    .sound(SoundType.DRIPSTONE_BLOCK))
            .blockstate(simpleCubeAll("heated_geothermal_vent"))
            .lang("Heated Geothermal Vent")
            .simpleItem()
            .register();

    public static final BlockEntry<GeothermalVentBlock> SUPERHEATED_VENT = REGISTRATE.block("superheated_geothermal_vent", GeothermalVentBlock::new)
            .initialProperties(SharedProperties::stone)
            .onRegister(easyConnectedTextures(AllSpriteShifts.SUPERHEATED_GEOTHERMAL_VENT))
            .properties(p -> p
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .lightLevel(s -> 12)
                    .sound(SoundType.BASALT))
            .blockstate(simpleCubeAll("superheated_geothermal_vent"))
            .lang("Superheated Geothermal Vent")
            .simpleItem()
            .register();
    public static final BlockEntry<Block> NIHILITE_DEPOSIT = REGISTRATE.block("nihilite_deposit", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.strength(-1.0F, 3600000.0F).noLootTable())
            .blockstate(simpleCubeAll("nihilite_deposit"))
            .simpleItem()
            .register();


    ////Steam Works
    //Steam Vent
    //TODO Put in models on blocks that need them, remove placeholders
    // Theese also include the weird stuff in place of a .simple item
    public static final BlockEntry<steamVentBlock> STEAM_VENT = REGISTRATE
            .block("steam_vent", steamVentBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().strength(3f))
            .simpleItem()
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .transform(pickaxeOnly())
            .blockstate(new ModelDirectionalBlockStateGen((ctx, prov, state) -> "block/steam_vent")::generate)
            .defaultLoot()
            .register();

    //Reinforcement
    public static final BlockEntry<Block> REINFORCEMENT = REGISTRATE
            .block("reinforcement", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.strength(2f))
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();


    //Turbine
    public static final BlockEntry<Block> TURBINE_EXTENSION = REGISTRATE
            .block("turbine_extension", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("turbine_extension"))
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> TURBINE_CENTER = REGISTRATE
            .block("turbine_center", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("turbine_center"))
            .defaultLoot()
            .register();

    public static final BlockEntry<turbineEndBlock> TURBINE_END = REGISTRATE
            .block("turbine_end", turbineEndBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .blockstate(new ModelDirectionalBlockStateGen((ctx, prov, state) -> "block/turbine_end")::generate)
            .defaultLoot()
            .register();
    //Condenser
    public static final BlockEntry<CondenserBlock> CONDENSER = REGISTRATE
            .block("condenser", CondenserBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelHorizontalDirectionalBlockStateGen((ctx, prov, state) -> "block/condenser")::generate)
            .defaultLoot()
            .register();
    //Heat Sink
    public static final BlockEntry<HeatSinkBlock> HEAT_SINK = REGISTRATE
            .block("heat_sink", HeatSinkBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p->p.noOcclusion().strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelSpunBlockStateGen((ctx, prov, state) -> "block/heat_sink")::generate)
            .defaultLoot()
            .register();

    //Cooler
    public static final BlockEntry<CoolerBlock> COOLER = REGISTRATE
            .block("cooler", CoolerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelHorizontalDirectionalBlockStateGen((ctx, prov, state) -> "block/cooler")::generate)
            .defaultLoot()
            .register();

    //Channel Core
    public static final BlockEntry<ChannelCoreBlock> CHANNEL_CORE = REGISTRATE
            .block("channel_core", ChannelCoreBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelDirectionalBlockStateGen((ctx, prov, state) -> "block/channel_core")::generate)
            .defaultLoot()
            .register();


    //Channel Expeller
    public static final BlockEntry<ChannelExpellerBlock> CHANNEL_EXPELLER = REGISTRATE
            .block("channel_expeller", ChannelExpellerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelDirectionalBlockStateGen((ctx, prov, state) -> "block/channel_expeller")::generate)
            .defaultLoot()
            .register();

    //Channel Block
    public static final BlockEntry<ChannelBlock> CHANNEL = REGISTRATE
            .block("chill_channel", ChannelBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .transform(pickaxeOnly())
            .simpleItem()
            .blockstate(new ModelDirectionalBlockStateGen((ctx, prov, state) -> "block/chill_channel")::generate)
            .defaultLoot()
            .register();
    //Chill Steel Coil
    public static final BlockEntry<Block> CHILLSTEEL_COIL = REGISTRATE
            .block("chillsteel_coil", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();

    //Cooling Tower
    public static final BlockEntry<CoolingTowerBlock> COOLING_TOWER = REGISTRATE
            .block("cooling_tower", CoolingTowerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelHorizontalDirectionalBlockStateGen((ctx, prov, state) -> "block/cooling_tower")::generate)
            .defaultLoot()
            .register();

    //Pressure Chamber

    public static final BlockEntry<ChamberCoreBlock> CHAMBER_CORE = REGISTRATE
            .block("pressure_chamber_core", ChamberCoreBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    public static final BlockEntry<CombustionVentBlock> COMBUSTION_VENT = REGISTRATE
            .block("combustion_vent", CombustionVentBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();
    public static final BlockEntry<Block> COMBUSTION_REGULATOR = REGISTRATE
            .block("combustion_regulator", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();




    //Impact Drill
    public static final BlockEntry<ImpactDrillBlock> IMPACT_DRILL = REGISTRATE
            .block("impact_drill", ImpactDrillBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/impact_drill")::generate)
            .defaultLoot()
            .register();

    //Gas Hood
    public static final BlockEntry<GasHoodBlock> GAS_HOOD = REGISTRATE
            .block("gas_hood", GasHoodBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelDirectionalBlockStateGen((ctx, prov, state) -> "block/gas_hood")::generate)
            .defaultLoot()
            .register();

    //Pressure Heater
    public static final BlockEntry<PressureHeaterBlock> PRESSURE_HEATER= REGISTRATE
            .block("pressure_heater", PressureHeaterBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .lang("Steam Heater")
            .register();

    //// Laser Optics

    //Blaze Crucible

    public static final BlockEntry<BlazeCrucibleBlock> BLAZE_CRUCIBLE = REGISTRATE
            .block("blaze_crucible", BlazeCrucibleBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().lightLevel(BlazeCrucibleBlock::getLight).strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/blaze_crucible")::generate)
            .defaultLoot()
            .register();

    //Diode
    public static final BlockEntry<DiodeBlock> DIODE = REGISTRATE
            .block("diode", DiodeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();


    //DIODE JUNCTION
    public static final BlockEntry<DiodeJunctionBlock> DIODE_JUNCTION = REGISTRATE
            .block("diode_junction", DiodeJunctionBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();


    //Laser Film
    public static final BlockEntry<Block> LASER_FILM = REGISTRATE
            .block("laser_film", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(2f))
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();


    //Anti-Laser Plating
    public static final BlockEntry<Block> ANTI_LASER_PLATING = REGISTRATE
            .block("antilaser_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.strength(5f))
            .simpleItem()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("antilaser_plating"))
            .defaultLoot()
            .register();

    //Mirrors
    public static final BlockEntry<mirrorBlock> BASIC_MIRROR = REGISTRATE
            .block("basic_mirror", mirrorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();


    //Dimmers
    public static final BlockEntry<Block> SUPERHEAT_DIMMER = REGISTRATE
            .block("superheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> OVERHEAT_DIMMER = REGISTRATE
            .block("overheat_dimmer", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("placeholder"))
            .defaultLoot()
            .register();
    //Thermometer
    public static final BlockEntry<ThermometerBlock> THERMOMETER = REGISTRATE
            .block("thermometer", ThermometerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Meter Extender
    public static final BlockEntry<MeterExtenderBlock> METER_EXTENDER = REGISTRATE
            .block("meter_extender", MeterExtenderBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.sound(SoundType.WOOD).strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .defaultLoot()
            .register();

    //Solar Panel
    //TODO make sure solar panels aren't transparent
    public static final BlockEntry<SolarPanelBlock> SOLAR_PANEL = REGISTRATE
            .block("solar_panel", SolarPanelBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.forceSolidOn().noOcclusion().strength(2f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/solar_panel")::generate)
            .register();


    //Blaze Absorber
    public static final BlockEntry<BlazeAbsorberBlock> BLAZE_ABSORBER = REGISTRATE
            .block("solar_absorber", BlazeAbsorberBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .simpleItem()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/solar_absorber")::generate)
            .register();

    public static final BlockEntry<GeothermalInterfaceBlock> GEOTHERMAL_INTERFACE = REGISTRATE
            .block("geothermal_interface", GeothermalInterfaceBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .register();




    /// Blowing Engine
    public static final BlockEntry<BlowingEngineBlock> BLOWING_ENGINE = REGISTRATE
            .block("blowing_engine", BlowingEngineBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .register();
    /// IBF
    //Reinforced Bricks
    public static final BlockEntry<Block> REINFORCED_BRICKS =
            REGISTRATE.block("reinforced_bricks", Block::new)
                    .initialProperties(() -> Blocks.BRICKS)
                    .properties(p -> p
                            .requiresCorrectToolForDrops()
                            .strength(3f))
                    .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
                    .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
                    .transform(pickaxeOnly())

                    .defaultLoot()
                    .lang("Reinforced Bricks")
                    .register();

    public static final BlockEntry<BlastFurnaceControllerBlock> INDUSTRIAL_BLAST_FURNACE_CONTROLLER = REGISTRATE
            .block("blast_furnace_controller", BlastFurnaceControllerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .register();
    public static final BlockEntry<FluidDuctBlock> FLUID_DUCT = REGISTRATE
            .block("fluid_duct", FluidDuctBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .register();
    public static final BlockEntry<ItemDuctBlock> ITEM_DUCT = REGISTRATE
            .block("item_duct", ItemDuctBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.strength(3f))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .defaultLoot()
            .transform(pickaxeOnly())
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
            .register();

    /// Fuel Rods & Regulators
    public static final BlockEntry<FuelRodBlock> URANIUM_FUEL_ROD = REGISTRATE
            .block("uranium_fuel_rod", FuelRodBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.strength(3f))
            .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
            .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)            .defaultLoot()
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<ControlRodBlock> CONTROL_ROD =
            REGISTRATE.block("control_rod", ControlRodBlock::new)
                    .initialProperties(() -> Blocks.IRON_BLOCK)
                    .properties(p -> p
                            .requiresCorrectToolForDrops()
                            .strength(2f))
                    .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
                    .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)                    .transform(pickaxeOnly())
                    .transform(pickaxeOnly())
                    .defaultLoot()
                    .lang("Control Rod")
                    .register();

    /// Radiolyzer
    public static final BlockEntry<RadiolyzerBlock> RADIOLYZER =
            REGISTRATE.block("radiolyzer", RadiolyzerBlock::new)
                    .initialProperties(() -> Blocks.COPPER_BLOCK)
                    .properties(p -> p
                            .requiresCorrectToolForDrops()
                            .strength(2f))
                    .simpleItem()
                    .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/radiolyzer")::generate)                    .transform(pickaxeOnly())
                    .transform(pickaxeOnly())
                    .defaultLoot()
                    .lang("Radiolyzer")
                    .register();

    /// Intake Filter
    public static final BlockEntry<Block> INTAKE_FILTER =
            REGISTRATE.block("intake_filter", Block::new)
                    .initialProperties(() -> Blocks.WHITE_WOOL)
                    .properties(p -> p
                            .requiresCorrectToolForDrops())
                    .item().model((ctx,prov) -> prov.getExistingFile(Overheated.asResource("placeholder"))).build()
                    .blockstate(new ModelBlockStateGen((ctx, prov, state) -> "block/place_holder")::generate)
                    .defaultLoot()
                    .lang("Reinforced Bricks")
                    .register();
    /// Everything else


    public static void register(){

    }

}
