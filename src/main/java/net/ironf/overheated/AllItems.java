package net.ironf.overheated;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.nuclear.radiation.GeigerCounterItem;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static net.ironf.overheated.Overheated.REGISTRATE;
import static net.ironf.overheated.utility.registration.OverheatedRegistrate.defaultMeltingRequirement;
import static net.ironf.overheated.utility.registration.OverheatedRegistrate.defaultMoltenProperties;

public class AllItems {
    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    //Industrial Sheet Stuff
    public static final ItemEntry<Item> INDUSTRIAL_SHEET = craftingIngredient("industrial_sheet","Industrial Sheet");
    public static final ItemEntry<Item> INCOMPLETE_INDUSTRIAL_SHEET = craftingIngredient("incomplete_industrial_sheet","Incomplete Industrial Sheet");

    //Nihilite Stuff
    public static final OverheatedRegistrate.MetallicSet NIHILITE =
            REGISTRATE.MakeMetallicSet("Nihilite", p -> p.mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .strength(5f));

    public static final ItemEntry<Item> NIHILTE_GLOBULE = craftingIngredient("nihilite_globule","Nihilite Globule");
    public static final ItemEntry<Item> CRUSHED_NIHILITE = craftingIngredient("crushed_nihilite","Crushed Nihilite");


    //Blazesteel and Laser stuff
    public static final OverheatedRegistrate.MetallicSet BLAZESTEEL =
            REGISTRATE.MakeMetallicSet("Blazesteel", p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANCIENT_DEBRIS)
                    .strength(5f));

    public static final ItemEntry<Item> BLAZEGLASS_FIXTURE = craftingIngredient("blazeglass_fixture","Blazeglass Fixture");
    public static final ItemEntry<Item> ANTILASER_PLATE = craftingIngredient("antilaser_plate","Anti-laser Plate");
    public static final ItemEntry<Item> INCOMPLETE_LASER_CASING = craftingIngredient("incomplete_laser_casing","Incomplete Laser Casing");


    //Salt Stuff
    public static final ItemEntry<Item> WHITE_HALITE = craftingIngredient("white_halite","White Halite");
    public static final ItemEntry<Item> RED_HALITE = craftingIngredient("red_halite","Red Halite");
    public static final ItemEntry<Item> BLUE_HALITE = craftingIngredient("blue_halite","Blue Halite");
    public static final ItemEntry<Item> RAW_SALT = craftingIngredient("raw_salt","Raw Salt");
    public static final ItemEntry<Item> TABLE_SALT = craftingIngredient("table_salt","Table Salt");
    public static final ItemEntry<Item> CHLORINE_CRYSTAL = craftingIngredient("chlorine_crystal","Chlorine Crystal");

    //Zombie Meat
    public static final ItemEntry<Item> RAW_ZOMBIE_MEAT = REGISTRATE.item("raw_zombie_meat",Item::new)
            .properties(
                    p -> p.food(new FoodProperties.Builder().nutrition(4).meat().saturationMod(0.1f).effect(new MobEffectInstance(MobEffects.HUNGER,600,0),0.35F).build()))
            .register();

    public static final ItemEntry<Item> COOKED_ZOMBIE_MEAT = REGISTRATE.item("cooked_zombie_meat",Item::new)
            .properties(
                    p -> p.food(new FoodProperties.Builder().nutrition(10).meat().saturationMod(0.4f).build()))
            .register();

    //Steamed Hams
    public static final ItemEntry<Item> STEAMED_HAM = REGISTRATE.item("steamed_ham",Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(8).meat().saturationMod(0.6f).build()))
            .register();

    public static final ItemEntry<Item> STEAMED_HAM_SANDWICH = REGISTRATE.item("steamed_ham_sandwich",Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(13).meat().saturationMod(0.6f).build()))
            .register();

    //Steam Stuff
    public static final ItemEntry<Item> INCOMPLETE_PRESSURIZED_CASING = craftingIngredient("incomplete_pressurized_casing","Incomplete Pressurized Casing");
    public static final ItemEntry<Item> TURBINE_COMPONENTS = craftingIngredient("turbine_components","Turbine Components");



    //Chillsteel

    public static final OverheatedRegistrate.MetallicSet CHILLSTEEL =
            REGISTRATE.MakeMetallicSet("Chillsteel", p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.POWDER_SNOW)
                    .strength(3.5f));

    public static final ItemEntry<Item> ICE_DIAMOND = craftingIngredient("ice_diamond","Ice Diamond");
    public static final ItemEntry<Item> CHANNEL_RELAY = craftingIngredient("chill_channel_relay","Channel Chill Relay");

    //Geothermium Stuff
    public static final ItemEntry<Item> GEOTHERMIUM_CHUNK = craftingIngredient("geothermium_chunk","Geothermium Chunk");
    public static final ItemEntry<Item> NETHER_GEOTHERMIUM_CHUNK = craftingIngredient("nether_geothermium_chunk","Nether Geothermium Chunk");
    public static final ItemEntry<Item> GEOTHERMIUM_POWDERS = craftingIngredient("geothermium_powders","Geothermium Powders");
    public static final ItemEntry<Item> NETHER_GEOTHERMIUM_POWDERS = craftingIngredient("nether_geothermium_powders","Nether Geothermium Powders");

    //Redstonite
    public static final ItemEntry<Item> REDSTONITE_CRYSTAL = craftingIngredient("redstonite_crystal","Redstonite Crystal");


    //Steel
    public static final OverheatedRegistrate.MetallicSet STEEL =
            REGISTRATE.MakeMetallicSet("Steel", p -> p.mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .strength(5f));

    public static final ItemEntry<Item> BATTERY_CATHODE = craftingIngredient("battery_cathode","Battery Cathode");
    public static final ItemEntry<Item> BATTERY_ANODE = craftingIngredient("battery_anode","Battery Anode");

    //Molten Metals/Casts
    public static final OverheatedRegistrate.MetallicSet ZINC_METALWORKS =
            REGISTRATE.MakeMetallicSet("Zinc",null,
                    defaultMoltenProperties,defaultMeltingRequirement,
                    com.simibubi.create.AllItems.ZINC_INGOT, com.simibubi.create.AllItems.ZINC_NUGGET, AllBlocks.ZINC_BLOCK);

    public static final OverheatedRegistrate.MetallicSet BRASS_METALWORKS =
            REGISTRATE.MakeMetallicSet("Brass",null,
                    defaultMoltenProperties,defaultMeltingRequirement,
                    com.simibubi.create.AllItems.BRASS_INGOT, com.simibubi.create.AllItems.BRASS_NUGGET, AllBlocks.BRASS_BLOCK);

    public static final OverheatedRegistrate.vanillaMetallicSet IRON_METALWORKS =
            REGISTRATE.makeVanillaMetallicSet("Iron",Items.IRON_INGOT,Items.IRON_NUGGET,Items.IRON_BLOCK);
    public static final OverheatedRegistrate.vanillaMetallicSet GOLD_METALWORKS =
            REGISTRATE.makeVanillaMetallicSet("Gold",Items.GOLD_INGOT,Items.GOLD_NUGGET,Items.GOLD_BLOCK);
    public static final OverheatedRegistrate.vanillaMetallicSet COPPER_METALWORKS =
            REGISTRATE.makeVanillaMetallicSet("Copper",Items.COPPER_INGOT, null,Items.COPPER_BLOCK);



    //Casts
    public static final ItemEntry<Item> EMPTY_SAND_CAST = craftingIngredient("empty_sand_cast","Empty Sand Cast");
    public static final ItemEntry<Item> EMPTY_GOLD_CAST = craftingIngredient("empty_gold_cast","Empty Gold Cast");

    //Geiger Counter
    public static final ItemEntry<GeigerCounterItem> GeigerCounter = REGISTRATE.item("geiger_counter", GeigerCounterItem::new)
            .properties(p -> p.stacksTo(1))
            .lang("Geiger Counter")
            .register();


    //Incomplete Items
    public static final ItemEntry<Item> INCOMPLETE_CATHODE = craftingIngredient("incomplete_cathode","Incomplete Battery Cathode");
    public static final ItemEntry<Item> INCOMPLETE_ANODE = craftingIngredient("incomplete_anode","Incomplete Battery Anode");


    public static ItemEntry<Item> craftingIngredient(String id,String lang){
        return REGISTRATE.item(id,Item::new).lang(lang).register();
    }


    public static void register(){

    }
}
