package net.ironf.overheated;

import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.laserOptics.Diode.DiodeBlockEntity;
import net.ironf.overheated.laserOptics.Diode.DiodeBlockEntityRenderer;
import net.ironf.overheated.laserOptics.Diode.DiodeCogInstance;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.laserOptics.solarPanel.SolarPanelBlockEntity;
import net.ironf.overheated.laserOptics.thermometer.ThermometerBlock;
import net.ironf.overheated.laserOptics.thermometer.ThermometerBlockEntity;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndRenderer;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlockEntities {

    //Turbine
    public static final BlockEntityEntry<turbineEndBlockEntity> TURBINE_END = REGISTRATE
            .blockEntity("turbine_end", turbineEndBlockEntity::new)
            .instance(() -> HalfShaftInstance::new, false)
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
            .instance(() -> DiodeCogInstance::new)
            .renderer(() -> DiodeBlockEntityRenderer::new)
            .validBlocks(AllBlocks.DIODE)
            .register();
    //Thermometer
    public static final BlockEntityEntry<ThermometerBlockEntity> THERMOMETER = REGISTRATE
            .blockEntity("thermometer", ThermometerBlockEntity::new)
            .validBlocks(AllBlocks.THERMOMETER)
            .register();
    //Solar Panel
    public static final BlockEntityEntry<SolarPanelBlockEntity> SOLAR_PANEL = REGISTRATE
            .blockEntity("solar_panel", SolarPanelBlockEntity::new)
            .validBlocks(AllBlocks.SOLAR_PANEL)
            .register();
    public static void register(){

    }
}
