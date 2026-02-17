package net.ironf.overheated.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.Ponder;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.ponder.scenes.SteamVentScene;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class OverheatedPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return Overheated.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderPlugin.super.registerScenes(helper);
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        HELPER.forComponents(AllBlocks.STEAM_VENT)
                .addStoryBoard("steam_vent", SteamVentScene::mainScene, BOILER_ATTACHMENTS);

    }

    ResourceLocation BOILER_ATTACHMENTS = Overheated.asResource("boiler_attachments");

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderPlugin.super.registerTags(helper);

        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
                CatnipServices.REGISTRIES::getKeyOrThrow);


        helper.registerTag(BOILER_ATTACHMENTS)
                .addToIndex()
                .item(com.simibubi.create.AllBlocks.FLUID_TANK.get(), true, false)
                .title("Boiler Attachments & Heaters")
                .description("Components which heat boilers, or attach to them")
                .register();
        HELPER.addToTag(BOILER_ATTACHMENTS)
                .add(com.simibubi.create.AllBlocks.STEAM_ENGINE)
                .add(AllBlocks.STEAM_VENT)
                .add(com.simibubi.create.AllBlocks.STEAM_WHISTLE)
                .add(com.simibubi.create.AllBlocks.BLAZE_BURNER)
                .add(AllBlocks.BLAZE_CRUCIBLE)
                .add(AllBlocks.BLAZE_ABSORBER);
    }
}
