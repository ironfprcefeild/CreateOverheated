package net.ironf.overheated;

import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
import net.ironf.overheated.laserOptics.backend.beam.BeamBlockEntity;
import net.ironf.overheated.laserOptics.backend.beam.laserCyst.LaserCystBlockEntity;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineCenter.turbineCenterBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndRenderer;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlockEntities {

    //Turbine
    public static final BlockEntityEntry<turbineCenterBlockEntity> TURBINE_CENTER = REGISTRATE
            .blockEntity("turbine_center", turbineCenterBlockEntity::new)
            .validBlocks(AllBlocks.TURBINE_CENTER)
            .register();
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


    //Laser Beam
    public static final BlockEntityEntry<BeamBlockEntity> BEAM = REGISTRATE
            .blockEntity("laser_beam", BeamBlockEntity::new)
            .validBlocks(AllBlocks.BEAM)
            .register();

    //Laser Cyst
    public static final BlockEntityEntry<LaserCystBlockEntity> LASER_CYST = REGISTRATE
            .blockEntity("laser_cyst", LaserCystBlockEntity::new)
            .validBlocks(AllBlocks.LASER_CYST)
            .register();

    //Blaze Crucible
    public static final BlockEntityEntry<BlazeCrucibleBlockEntity> BLAZE_CRUCIBLE = REGISTRATE
            .blockEntity("blaze_crucible", BlazeCrucibleBlockEntity::new)
            .validBlocks(AllBlocks.BLAZE_CRUCIBLE)
            .register();

    public static void register(){
        BlazeCrucibleBlockEntity.addToBoilerHeaters();
        DiodeHeaters.registerDefaults();
    }
}
