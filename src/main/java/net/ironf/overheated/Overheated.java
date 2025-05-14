package net.ironf.overheated;

import com.mojang.logging.LogUtils;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlockEntity;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.ironf.overheated.recipes.AllRecipes;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.ironf.overheated.worldgen.AllFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Overheated.MODID)
public class Overheated
{
    public static final String MODID = "coverheated";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final OverheatedRegistrate REGISTRATE = new OverheatedRegistrate(MODID);
    public Overheated()
    {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        MinecraftForge.EVENT_BUS.register(this);

        //CTOR
        REGISTRATE.registerEventListeners(modEventBus);
        AllTags.init();
        AllCreativeModeTabs.register(modEventBus);
        AllFluids.register();
        AllBlocks.register();
        AllItems.register();
        AllBlockEntities.register();
        AllRecipes.register(modEventBus);
        AllFeatures.register();
        modEventBus.addListener(Overheated::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OverheatedClient.onCtorClient(modEventBus, forgeEventBus));


    }


    public static void init(final FMLCommonSetupEvent event)
    {
        LOGGER.info("...OVERHEATING...");
        LOGGER.info("Thank you for choosing Create: Overheated!");
        GasMapper.prepareGasBlockInfo();
        AllSteamFluids.prepareSteamArray();
        BlazeCrucibleBlockEntity.addToBoilerHeaters();
        DiodeHeaters.registerDefaults();
        mirrorRegister.registerDefaults();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Overheated is running on the server");
        CoolingHandler.setLevel(event.getServer().overworld());
        CoolingHandler.generateHandler();
        CondensingRecipeHandler.setLevel(event.getServer().overworld());
        CondensingRecipeHandler.generateHandler();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code

        }


    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }


}
