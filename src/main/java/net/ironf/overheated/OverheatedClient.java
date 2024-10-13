package net.ironf.overheated;

import com.simibubi.create.content.decoration.encasing.CasingConnectivity;
import com.simibubi.create.foundation.utility.ModelSwapper;
import net.minecraftforge.eventbus.api.IEventBus;

public class OverheatedClient {
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
    public static final CasingConnectivity CASING_CONNECTIVITY = new CasingConnectivity();
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        MODEL_SWAPPER.registerListeners(modEventBus);
    }
}
