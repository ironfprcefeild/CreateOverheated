package net.ironf.overheated;

import net.createmod.ponder.foundation.PonderIndex;
import net.ironf.overheated.gasses.GasBlock;
import net.ironf.overheated.ponder.OverheatedPonderPlugin;
import net.ironf.overheated.utility.registration.AllSpriteShifts;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(value = Overheated.MODID, dist = Dist.CLIENT)
public class OverheatedClient {

    public OverheatedClient(IEventBus modEventBus){
        onCtorClient(modEventBus);
    }

    public static void onCtorClient(IEventBus modEventBus) {
        modEventBus.addListener(OverheatedClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        AllPartialModels.init();
        AllSpriteShifts.init();
        PonderIndex.addPlugin(new OverheatedPonderPlugin());
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