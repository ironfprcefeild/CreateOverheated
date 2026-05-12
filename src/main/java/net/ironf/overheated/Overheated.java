package net.ironf.overheated;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.GasBlock;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.ironf.overheated.laserOptics.solarPanel.blazeAbsorber.BlazeAbsorberBlockEntity;
import net.ironf.overheated.nuclear.radiation.RadiationMap;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler;
import net.ironf.overheated.utility.TranslucencyHandler;
import net.ironf.overheated.utility.data.dataGeneration.recipes.OverheatedRecipeProvider;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.ironf.overheated.worldgen.AllFeatures;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Overheated.MODID)
@EventBusSubscriber
public class Overheated
{
    public static final String MODID = "coverheated";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final OverheatedRegistrate REGISTRATE = new OverheatedRegistrate(MODID);


    public Overheated()
    {
        //Theee Errors are just here cause of deprecation
        //If anyone knows what I'm supposed to do please tell me.
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = ModLoadingContext.get()
                .getActiveContainer().getEventBus();
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        //Events
        NeoForge.EVENT_BUS.register(this);
        RadiationMap.subscribeEvents(NeoForge.EVENT_BUS);

        //CTOR
        REGISTRATE.registerEventListeners(modEventBus);
        AllTags.init();
        AllFluids.register();
        AllBlocks.register();
        AllItems.register();
        AllBlockEntities.register();
        AllRecipes.register(modEventBus);
        AllFeatures.register();
        AllCreativeModeTabs.register(modEventBus);
        modEventBus.addListener(Overheated::init);
        OverheatedDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OverheatedClient.onCtorClient(modEventBus, forgeEventBus));


    }

    public static void init(final FMLCommonSetupEvent event)
    {
        LOGGER.info("...OVERHEATING...");
        LOGGER.info("Thank you for choosing Create: Overheated!");
        LOGGER.info("\"O\" indicates a log message from Overheated");
        GasMapper.prepareGasBlockInfo();
        AllSteamFluids.prepareSteamArray();
        BlazeCrucibleBlockEntity.addToBoilerHeaters();
        BlazeAbsorberBlockEntity.addToBoilerHeaters();
        DiodeHeaters.registerDefaults();
        mirrorRegister.registerDefaults();
        ControlRodsRegister.registerDefaults();
        TranslucencyHandler.addRenderLayers();

        //(Un)Mysterious Conversion
        OverheatedRecipeProvider.addMysteriousConversion();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Overheated is running on the server");
        LOGGER.info("\"SO\" indicates a log message from Overheated, on the Server");
        CoolingHandler.setLevel(event.getServer().overworld());
        CoolingHandler.generateHandler();
        CondensingRecipeHandler.setLevel(event.getServer().overworld());
        CondensingRecipeHandler.generateHandler();
        RadiationMap.RadiationHashMap.clear();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppedEvent event){
        LOGGER.info("SO: Overheated is closing on the server");
        RadiationMap.RadiationHashMap.clear();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        AllCapabilities.RegisterAllCapabilities(event);
    }



        // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code

        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
            for (DeferredHolder<Block, ? extends GasBlock> gb : OverheatedRegistrate.blockTintColors.keySet()){
                event.register(getBlockColor(OverheatedRegistrate.blockTintColors.get(gb)),gb.get());
            }
        }

        public static BlockColor getBlockColor(int tintColor){
            return (p_92567_, p_92568_, p_92569_, p_92570_) -> tintColor;
        }

    }

    public static ResourceLocation asResource(String path) {
        return fromNamespaceAndPath(MODID, path);
    }


}
