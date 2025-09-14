package net.ironf.overheated;

import com.simibubi.create.content.kinetics.base.HalfShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.cooling.chillChannel.core.ChannelCoreBlockEntity;
import net.ironf.overheated.cooling.chillChannel.node.absorber.ChannelAbsorberBlockEntity;
import net.ironf.overheated.cooling.chillChannel.node.expeller.ChannelExpellerBlockEntity;
import net.ironf.overheated.cooling.cooler.CoolerBlockEntity;
import net.ironf.overheated.cooling.coolingTower.CoolingTowerBlockEntity;
import net.ironf.overheated.gasses.GasHood.GasHoodBlockEntity;
import net.ironf.overheated.laserOptics.Diode.DiodeBlockEntity;
import net.ironf.overheated.laserOptics.Diode.DiodeBlockEntityRenderer;
import net.ironf.overheated.laserOptics.Diode.DiodeCogInstance;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.laserOptics.mirrors.splitMirror.SplitMirrorBlockEntity;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlockEntity;
import net.ironf.overheated.laserOptics.solarPanel.blazeAbsorber.BlazeAbsorberBlockEntity;
import net.ironf.overheated.laserOptics.thermometer.ThermometerBlockEntity;
import net.ironf.overheated.steamworks.blocks.condensor.CondenserBlockEntity;
import net.ironf.overheated.steamworks.blocks.geothermals.GeothermalInterfaceBlockEntity;
import net.ironf.overheated.cooling.heatsink.HeatSinkBlockEntity;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillBlockEntity;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillRenderer;
import net.ironf.overheated.steamworks.blocks.meterExtender.MeterExtenderBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureHeater.PressureHeaterBlockEntity;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndRenderer;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlockEntities {

    //Turbine
    public static final BlockEntityEntry<turbineEndBlockEntity> TURBINE_END = REGISTRATE
            .blockEntity("turbine_end", turbineEndBlockEntity::new)
            .visual(() -> HalfShaftVisual::new, false)
            .renderer(() -> turbineEndRenderer::new)
            .validBlocks(AllBlocks.TURBINE_END)
            .register();

    //Steam vent

    public static final BlockEntityEntry<steamVentBlockEntity> STEAM_VENT = REGISTRATE
            .blockEntity("steam_vent", steamVentBlockEntity::new)
            .validBlocks(AllBlocks.STEAM_VENT)
            .register();


    //Blaze Crucible
    public static final BlockEntityEntry<BlazeCrucibleBlockEntity> BLAZE_CRUCIBLE = REGISTRATE
            .blockEntity("blaze_crucible", BlazeCrucibleBlockEntity::new)
            .validBlocks(AllBlocks.BLAZE_CRUCIBLE)
            .register();

    //Laser Diode
    public static final BlockEntityEntry<DiodeBlockEntity> DIODE = REGISTRATE
            .blockEntity("diode", DiodeBlockEntity::new)
            .visual(() -> DiodeCogInstance::new)
            .renderer(() -> DiodeBlockEntityRenderer::new)
            .validBlocks(AllBlocks.DIODE)
            .register();

    //Split Mirror
    public static final BlockEntityEntry<SplitMirrorBlockEntity> SPLIT_MIRROR = REGISTRATE
            .blockEntity("split_mirror", SplitMirrorBlockEntity::new)
            .validBlocks(AllBlocks.SPLIT_MIRROR)
            .register();
    //Thermometer
    public static final BlockEntityEntry<ThermometerBlockEntity> THERMOMETER = REGISTRATE
            .blockEntity("thermometer", ThermometerBlockEntity::new)
            .validBlocks(AllBlocks.THERMOMETER)
            .register();

    //Meter Extender
    public static final BlockEntityEntry<MeterExtenderBlockEntity> METER_EXTENDER = REGISTRATE
            .blockEntity("meter_extender", MeterExtenderBlockEntity::new)
            .validBlocks(AllBlocks.METER_EXTENDER)
            .register();

    //Solar Panel
    public static final BlockEntityEntry<SolarPanelBlockEntity> SOLAR_PANEL = REGISTRATE
            .blockEntity("solar_panel", SolarPanelBlockEntity::new)
            .validBlocks(AllBlocks.SOLAR_PANEL)
            .register();

    //Blaze Absorber
    public static final BlockEntityEntry<BlazeAbsorberBlockEntity> BLAZE_ABSORBER = REGISTRATE
            .blockEntity("blaze_absorber", BlazeAbsorberBlockEntity::new)
            .validBlocks(AllBlocks.BLAZE_ABSORBER)
            .register();

    //Heat Sink
    public static final BlockEntityEntry<HeatSinkBlockEntity> HEAT_SINK = REGISTRATE
            .blockEntity("heat_sink", HeatSinkBlockEntity::new)
            .validBlocks(AllBlocks.HEAT_SINK)
            .register();

    //Cooler
    public static final BlockEntityEntry<CoolerBlockEntity> COOLER = REGISTRATE
            .blockEntity("cooler", CoolerBlockEntity::new)
            .validBlocks(AllBlocks.COOLER)
            .register();

    //Chill Channel Core
    public static final BlockEntityEntry<ChannelCoreBlockEntity> CHANNEL_CORE = REGISTRATE
            .blockEntity("channel_core", ChannelCoreBlockEntity::new)
            .validBlocks(AllBlocks.CHANNEL_CORE)
            .register();

    //Chill Channel Absorber
    public static final BlockEntityEntry<ChannelAbsorberBlockEntity> CHANNEL_ABSORBER = REGISTRATE
            .blockEntity("channel_absorber", ChannelAbsorberBlockEntity::new)
            .validBlocks(AllBlocks.CHANNEL_ABSORBER)
            .register();

    //Chill Channel Expeller
    public static final BlockEntityEntry<ChannelExpellerBlockEntity> CHANNEL_EXPELLER = REGISTRATE
            .blockEntity("channel_expeller", ChannelExpellerBlockEntity::new)
            .validBlocks(AllBlocks.CHANNEL_EXPELLER)
            .register();

    //Cooling Tower
    public static final BlockEntityEntry<CoolingTowerBlockEntity> COOLING_TOWER = REGISTRATE
            .blockEntity("cooling_tower", CoolingTowerBlockEntity::new)
            .validBlocks(AllBlocks.COOLING_TOWER)
            .register();
    //Condenser
    public static final BlockEntityEntry<CondenserBlockEntity> CONDENSER = REGISTRATE
            .blockEntity("condenser", CondenserBlockEntity::new)
            .validBlocks(AllBlocks.CONDENSER)
            .register();

    //Impact Drill
    public static final BlockEntityEntry<ImpactDrillBlockEntity> IMPACT_DRILL = REGISTRATE
            .blockEntity("impact_drill", ImpactDrillBlockEntity::new)
            .validBlocks(AllBlocks.IMPACT_DRILL)
            .renderer(() -> ImpactDrillRenderer::new)
            .register();

    //GasHood
    public static final BlockEntityEntry<GasHoodBlockEntity> GAS_HOOD = REGISTRATE
            .blockEntity("gas_hood", GasHoodBlockEntity::new)
            .validBlocks(AllBlocks.GAS_HOOD)
            .register();

    //Geothermal Interface
    public static final BlockEntityEntry<GeothermalInterfaceBlockEntity> GEOTHERMAL_INTERFACE = REGISTRATE
            .blockEntity("geothermal_interface", GeothermalInterfaceBlockEntity::new)
            .validBlocks(AllBlocks.GEOTHERMAL_INTERFACE)
            .register();
    //Pressure Heater
    public static final BlockEntityEntry<PressureHeaterBlockEntity> PRESSURE_HEATER = REGISTRATE
            .blockEntity("pressure_heater", PressureHeaterBlockEntity::new)
            .validBlocks(AllBlocks.PRESSURE_HEATER)
            .register();

    ///Pressure Chamber

    //Core
    public static final BlockEntityEntry<ChamberCoreBlockEntity> CHAMBER_CORE = REGISTRATE
            .blockEntity("pressure_chamber_core", ChamberCoreBlockEntity::new)
            .validBlocks(AllBlocks.CHAMBER_CORE)
            .register();


    public static void register(){

    }
}
