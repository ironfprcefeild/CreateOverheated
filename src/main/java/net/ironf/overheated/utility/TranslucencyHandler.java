package net.ironf.overheated.utility;

import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.AllFluids;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class TranslucencyHandler {
    public static void addRenderLayers(){
        ItemBlockRenderTypes.setRenderLayer(AllBlocks.BLAZEGLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AllBlocks.BLAZEGLASS_PANE.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(AllSteamFluids.DISTILLED_WATER.FLUID_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AllFluids.PURIFIED_WATER.FLUID_BLOCK.get(), RenderType.translucent());

        OverheatedRegistrate.applyGasTransparency();


    }
}
