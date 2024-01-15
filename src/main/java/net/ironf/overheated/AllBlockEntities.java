package net.ironf.overheated;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
import net.ironf.overheated.laserOptics.backend.beam.BeamBlockEntity;
import net.ironf.overheated.laserOptics.backend.beam.laserCyst.LaserCystBlockEntity;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlock;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineInterface.turbineInterfaceBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineShaft.turbineShaftBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineVent.turbineVentBlockEntity;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlockEntities {


    //Steam vent

    public static final BlockEntityEntry<steamVentBlockEntity> STEAM_VENT = REGISTRATE
            .blockEntity("steam_vent", steamVentBlockEntity::new)
            .validBlocks(AllBlocks.STEAM_VENT)
            .register();

    //Turbine
    public static final BlockEntityEntry<turbineBlockEntity> TURBINE = REGISTRATE
            .blockEntity("turbine", turbineBlockEntity::new)
            .validBlocks(AllBlocks.TURBINE)
            .register();


    //Turbine Shaft
    public static final BlockEntityEntry<turbineShaftBlockEntity> TURBINE_SHAFT = REGISTRATE
            .blockEntity("turbine_shaft", turbineShaftBlockEntity::new)
            .validBlocks(AllBlocks.TURBINE_SHAFT)
            .register();
    //Turbine Vent
    public static final BlockEntityEntry<turbineVentBlockEntity> TURBINE_VENT = REGISTRATE
            .blockEntity("turbine_vent", turbineVentBlockEntity::new)
            .validBlocks(AllBlocks.TURBINE_VENT)
            .register();

    //Turbine Interface
    public static final BlockEntityEntry<turbineInterfaceBlockEntity> TURBINE_INTERFACE = REGISTRATE
            .blockEntity("turbine_interface", turbineInterfaceBlockEntity::new)
            .validBlocks(AllBlocks.TURBINE_INTERFACE)
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
