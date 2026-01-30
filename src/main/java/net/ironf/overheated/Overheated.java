package net.ironf.overheated;

import com.mojang.logging.LogUtils;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
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
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.ironf.overheated.worldgen.AllFeatures;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Overheated.MODID)
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
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        MinecraftForge.EVENT_BUS.register(this);

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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OverheatedClient.onCtorClient(modEventBus, forgeEventBus));

        //This changes the default stress impact of the flywheel block



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

        //Mysterious Conversion
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(
                AllFluids.SLUDGE.BUCKET.get().getDefaultInstance(),AllFluids.STRAY_SAUCE.BUCKET.get().getDefaultInstance()));

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
        loadRadiationInformation(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppedEvent event){
        LOGGER.info("SO: Overheated is closing on the server");
        saveRadiationInformation(event.getServer());
    }

    public void saveRadiationInformation(MinecraftServer server){
        LOGGER.info("SO: Saving Radiation Info");
        RadiationMap data = RadiationMap.manage(server);
        data.setRadiationMap(RadiationMap.RadiationHashMap);
    }
    public void loadRadiationInformation(MinecraftServer server){
        LOGGER.info("SO: Loading Radiation Info");
        RadiationMap data = RadiationMap.manage(server);
        RadiationMap.RadiationHashMap = data.getRadiationHashMap();
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

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
            for (RegistryObject<GasBlock> gb : OverheatedRegistrate.blockTintColors.keySet()){
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
