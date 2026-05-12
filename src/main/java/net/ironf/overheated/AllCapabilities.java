package net.ironf.overheated;

import net.ironf.overheated.cooling.chillChannel.core.ChannelCoreBlockEntity;
import net.ironf.overheated.cooling.cooler.CoolerBlockEntity;
import net.ironf.overheated.laserOptics.Diode.DiodeBlockEntity;
import net.ironf.overheated.metalWorking.bellow.BellowBlockEntity;
import net.ironf.overheated.nuclear.radiolyzer.RadiolyzerBlockEntity;
import net.ironf.overheated.steamworks.blocks.blowingEngine.BlowingEngineBlockEntity;
import net.ironf.overheated.steamworks.blocks.impactDrill.ImpactDrillBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.combustion.CombustionVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureHeater.PressureHeaterBlockEntity;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static net.ironf.overheated.AllBlockEntities.*;

public class AllCapabilities {

    public static void RegisterAllCapabilities(RegisterCapabilitiesEvent event){
        Overheated.LOGGER.info("O: Registering Capabilities on all Block Entities");
        //? extends CapableMachineBlockEntity
        steamVentBlockEntity.registerCapabilities(event,STEAM_VENT,true,false);
        CoolerBlockEntity.registerCapabilities(event,COOLER,true,false);
        ChannelCoreBlockEntity.registerCapabilities(event,CHANNEL_CORE,true,false);
        ImpactDrillBlockEntity.registerCapabilities(event,IMPACT_DRILL,true,false);
        PressureHeaterBlockEntity.registerCapabilities(event,PRESSURE_HEATER,true,false);
        ChamberCoreBlockEntity.registerCapabilities(event,CHAMBER_CORE,true,true);
        CombustionVentBlockEntity.registerCapabilities(event,COMBUSTION_VENT,true,false);
        BlowingEngineBlockEntity.registerCapabilities(event,BLOWING_ENGINE,true,false);
        RadiolyzerBlockEntity.registerCapabilities(event,RADIOLYZER,true,false);



        //Others
        turbineEndBlockEntity.registerCapabilities(event,TURBINE_END);
        BellowBlockEntity.registerCapabilities(event,BELLOW);
        DiodeBlockEntity.registerCapabilities(event,DIODE);
    }
}
