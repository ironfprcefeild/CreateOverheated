package net.ironf.overheated.creativeModeTab;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.*;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.AllItems;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.OverheatedDistExecutor;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class AllCreativeModeTabs {


    /// TODO remove log messages
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }


    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Overheated.MODID);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OVERHEATED_TAB = REGISTER.register("overheatedtab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.overheated.base"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(AllBlocks.STEAM_VENT::asStack)
                    .displayItems((parameters, output) -> {
                        output.acceptAll(collectItems());
                        output.acceptAll(collectBlocks());
                    })
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OVERHEATED_STEAM_BUCKETS_TAB = REGISTER.register("steambuckettab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.overheated.steam_bucket_tab"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> new ItemStack(AllSteamFluids.STEAM_INSANE.BUCKET.get(),1))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.acceptAll(collectBucketItems());
                    }))
                    .build());


    public static List<ItemStack> collectBlocks() {
        Overheated.LOGGER.info("CO: Collecting Blocks");

        List<ItemStack> items = new ReferenceArrayList<>();
        for (RegistryEntry<Block, ? extends Block> entry : Overheated.REGISTRATE.getAll(Registries.BLOCK)) {
            Item item = entry.get()
                    .asItem();
            if (item == Items.AIR  || OVERHEATED_TAB.get().contains(item.getDefaultInstance()))
                continue;
            Overheated.LOGGER.info("Adding to tab:"  + item);
            items.add(item.getDefaultInstance());
        }
        for (DeferredHolder<Item, ? extends Item> entry : OverheatedRegistrate.items_for_tab){
            Item item = entry.get();
            if (!(item instanceof BlockItem)  || OVERHEATED_TAB.get().contains(item.getDefaultInstance()))
                continue;
            Overheated.LOGGER.info("Adding to tab:"  + item);
            items.add(item.getDefaultInstance());
        }
        return items;
    }

    public static List<ItemStack> collectItems() {
        Overheated.LOGGER.info("CO: Collecting Items");

        List<ItemStack> items = new ReferenceArrayList<>();
        for (RegistryEntry<Item, ? extends Item> entry : Overheated.REGISTRATE.getAll(Registries.ITEM)) {
            Item item = entry.get();
            if (item instanceof BlockItem || OVERHEATED_TAB.get().contains(item.getDefaultInstance()))
                continue;
            Overheated.LOGGER.info("Adding to tab:"  + item);
            items.add(item.getDefaultInstance());

        }
        for (DeferredHolder<Item, ? extends Item> entry : OverheatedRegistrate.items_for_tab){
            Item item = entry.get();
            if (item instanceof BlockItem || OVERHEATED_TAB.get().contains(item.getDefaultInstance()))
                continue;
            Overheated.LOGGER.info("Adding to tab:"  + item);


            items.add(item.getDefaultInstance());

        }
        return items;
    }


    public static List<ItemStack> collectBucketItems() {
        Overheated.LOGGER.info("CO: Collecting Buckets");
        List<ItemStack> items = new ReferenceArrayList<>();
        for (DeferredHolder<Item, ? extends Item> entry : OverheatedRegistrate.allSteamBuckets){
            Item item = entry.get();
            if (item instanceof BlockItem)
                continue;
            items.add(item.getDefaultInstance());
        }
        return items;
    }

    /*
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OVERHEATED_TAB = REGISTER.register("overheatedtab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.overheated.base"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(AllBlocks.STEAM_VENT::asStack)
                    .displayItems(new RegistrateDisplayItemsGenerator(true, AllCreativeModeTabs.OVERHEATED_TAB,OverheatedRegistrate.items_for_tab))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OVERHEATED_STEAM_BUCKETS_TAB = REGISTER.register("steambuckettab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.overheated.steam_bucket_tab"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> new ItemStack(AllSteamFluids.STEAM_INSANE.BUCKET.get(),1))
                    .displayItems(new RegistrateDisplayItemsGenerator(false, AllCreativeModeTabs.OVERHEATED_STEAM_BUCKETS_TAB,OverheatedRegistrate.allSteamBuckets))
                    .build());





    private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        private static final Predicate<Item> IS_ITEM_3D_PREDICATE;

        static {
            MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
            OverheatedDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                isItem3d.setValue(item -> {
                    ItemRenderer itemRenderer = Minecraft.getInstance()
                            .getItemRenderer();
                    BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
                    return model.isGui3d();
                });
            });
            IS_ITEM_3D_PREDICATE = isItem3d.getValue();
        }


        private final boolean addItems;
        private final DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter;
        List<DeferredHolder<Item,? extends Item>> extraItems = null;

        public RegistrateDisplayItemsGenerator(boolean addItems, DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter) {
            this.addItems = addItems;
            this.tabFilter = tabFilter;
        }
        public RegistrateDisplayItemsGenerator(boolean addItems, DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter, List<DeferredHolder<Item,? extends Item>> ExtraItems) {
            this.addItems = addItems;
            this.tabFilter = tabFilter;
            this.extraItems = ExtraItems;
        }

        private static Predicate<Item> makeExclusionPredicate() {
            Set<Item> exclusions = new ReferenceOpenHashSet<>();

            //!!!!// Exclude Certain Items
            List<ItemProviderEntry<?,?>> simpleExclusions = List.of(
                    AllItems.INCOMPLETE_INDUSTRIAL_SHEET,
                    AllItems.INCOMPLETE_PRESSURIZED_CASING,
                   AllItems.INCOMPLETE_LASER_CASING
            );

            //!!!!// Exclude Certain Items by tag
            List<ItemEntry<TagDependentIngredientItem>> tagDependentExclusions = List.of(

            );

            for (ItemProviderEntry<?,?> entry : simpleExclusions) {
                exclusions.add(entry.asItem());
            }

            for (ItemEntry<TagDependentIngredientItem> entry : tagDependentExclusions) {
                TagDependentIngredientItem item = entry.get();
                if (item.shouldHide()) {
                    exclusions.add(entry.asItem());
                }
            }

            return exclusions::contains;
        }

        private static List<RegistrateDisplayItemsGenerator.ItemOrdering> makeOrderings() {
            List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings = new ReferenceArrayList<>();

            //!!!!// Put some items before others
            Map<ItemProviderEntry<?,?>, ItemProviderEntry<?,?>> simpleBeforeOrderings = new java.util.HashMap<>(Map.of(
                    AllBlocks.SUPERHEAT_DIMMER, AllBlocks.OVERHEAT_DIMMER,
                    AllBlocks.DIODE, AllBlocks.BLAZE_CRUCIBLE,
                    AllBlocks.TURBINE_CENTER, AllBlocks.TURBINE_END,
                    AllBlocks.TURBINE_END, AllBlocks.TURBINE_CENTER,
                    AllItems.RAW_ZOMBIE_MEAT, AllItems.COOKED_ZOMBIE_MEAT,
                    AllBlocks.GEOTHERMIUM, AllItems.GEOTHERMIUM_CHUNK,
                    AllItems.GEOTHERMIUM_CHUNK, AllItems.GEOTHERMIUM_POWDERS,
                    AllItems.GEOTHERMIUM_POWDERS, AllBlocks.NETHER_GEOTHERMIUM,
                    AllBlocks.NETHER_GEOTHERMIUM, AllItems.NETHER_GEOTHERMIUM_CHUNK,
                    AllItems.NETHER_GEOTHERMIUM_CHUNK, AllItems.NETHER_GEOTHERMIUM_POWDERS
            ));
            simpleBeforeOrderings.put(AllItems.COOKED_ZOMBIE_MEAT,AllItems.STEAMED_HAM);
            simpleBeforeOrderings.put(AllItems.STEAMED_HAM,AllItems.STEAMED_HAM_SANDWICH);

            //!!!!// Put some items after others
            Map<ItemProviderEntry<?,?>, ItemProviderEntry<?,?>> simpleAfterOrderings = Map.of(

            );

            simpleBeforeOrderings.forEach((entry, otherEntry) -> {
                orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
            });

            simpleAfterOrderings.forEach((entry, otherEntry) -> {
                orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
            });

            return orderings;
        }

        private static Function<Item, ItemStack> makeStackFunc() {
            Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

            //!!!!// Add custom item stacks for certain items for NBT data
            Map<ItemProviderEntry<?,?>, Function<Item, ItemStack>> simpleFactories = Map.of(

            );

            simpleFactories.forEach((entry, factory) -> {
                factories.put(entry.asItem(), factory);
            });

            return item -> {
                Function<Item, ItemStack> factory = factories.get(item);
                if (factory != null) {
                    return factory.apply(item);
                }
                return new ItemStack(item);
            };
        }

        private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
            Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();


            //!!!!// Add visibility functions for certain items, controlling in what tabs it can  be found (like search tab only)
            Map<ItemProviderEntry<?,?>, CreativeModeTab.TabVisibility> simpleVisibilities = Map.of(
            );


            simpleVisibilities.forEach((entry, factory) -> {
                visibilities.put(entry.asItem(), factory);
            });


            //!!!// Larger looping functions should be put below for dyed item sets or edge cases as too not clutter the creative mode tab


            return item -> {
                CreativeModeTab.TabVisibility visibility = visibilities.get(item);
                if (visibility != null) {
                    return visibility;
                }
                return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
            };
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
            Predicate<Item> exclusionPredicate = makeExclusionPredicate();
            List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings = makeOrderings();
            Function<Item, ItemStack> stackFunc = makeStackFunc();
            Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = makeVisibilityFunc();

            List<Item> items = new LinkedList<>();
            if (addItems) {
                items.addAll(collectItems(items,exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate())));
                items.addAll(collectBlocks(items,exclusionPredicate));
                items.addAll(collectItems(items,exclusionPredicate.or(IS_ITEM_3D_PREDICATE)));
            }

            if (extraItems != null) {
                for (DeferredHolder<Item,? extends Item> i : extraItems) {
                    items.add(i.get());
                }
            }

            applyOrderings(items, orderings);
            outputAll(output, items, stackFunc, visibilityFunc);
        }



        private static void applyOrderings(List<Item> items, List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings) {
            for (RegistrateDisplayItemsGenerator.ItemOrdering ordering : orderings) {
                int anchorIndex = items.indexOf(ordering.anchor());
                if (anchorIndex != -1) {
                    Item item = ordering.item();
                    int itemIndex = items.indexOf(item);
                    if (itemIndex != -1) {
                        items.remove(itemIndex);
                        if (itemIndex < anchorIndex) {
                            anchorIndex--;
                        }
                    }
                    if (ordering.type() == RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER) {
                        items.add(anchorIndex + 1, item);
                    } else {
                        items.add(anchorIndex, item);
                    }
                }
            }
        }

        private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
            for (Item item : items) {
                output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
            }
        }

        private record ItemOrdering(Item item, Item anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type type) {
            public static RegistrateDisplayItemsGenerator.ItemOrdering before(Item item, Item anchor) {
                return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type.BEFORE);
            }

            public static RegistrateDisplayItemsGenerator.ItemOrdering after(Item item, Item anchor) {
                return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER);
            }

            public enum Type {
                BEFORE,
                AFTER;
            }
        }
    }

     */
}
