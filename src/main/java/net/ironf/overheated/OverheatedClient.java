package net.ironf.overheated;

import com.mojang.datafixers.TypeRewriteRule;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.decoration.encasing.CasingConnectivity;
import com.simibubi.create.foundation.utility.ModelSwapper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class OverheatedClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(OverheatedClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        AllPartialModels.init();
        AllSpriteShifts.init();
    }

}