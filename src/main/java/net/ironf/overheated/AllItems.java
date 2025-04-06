package net.ironf.overheated;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.ironf.overheated.cooling.chillChannel.adjuster.ChannelWrenchItem;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.minecraft.world.item.Item;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllItems {
    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    //Industrial Sheet Stuff
    public static final ItemEntry<Item> INDUSTRIAL_SHEET = craftingIngredient("industrial_sheet","Industrial Sheet");
    public static final ItemEntry<Item> INCOMPLETE_INDUSTRIAL_SHEET = craftingIngredient("incomplete_industrial_sheet","Incomplete Industrial Sheet");

    //Nihilite Stuff
    public static final ItemEntry<Item> NIHILTE_INGOT = craftingIngredient("nihilite_ingot","Nihilite Ingot");
    public static final ItemEntry<Item> NIHILTE_NUGGET = craftingIngredient("nihilite_nugget","Nihilite Nugget");
    public static final ItemEntry<Item> NIHILTE_GLOBULE = craftingIngredient("nihilite_globule","Nihilite Globule");
    public static final ItemEntry<Item> CRUSHED_NIHILITE = craftingIngredient("crushed_nihilite","Crushed Nihilite");


    //Blazesteel and Laser stuff
    public static final ItemEntry<Item> BLAZESTEEL_INGOT = craftingIngredient("blazesteel_ingot","Blazesteel Ingot");
    public static final ItemEntry<Item> BLAZESTEEL_NUGGET = craftingIngredient("blazesteel_nugget","Blazesteel Nugget");
    public static final ItemEntry<Item> BLAZEGLASS_FIXTURE = craftingIngredient("blazeglass_fixture","Blazeglass Fixture");
    public static final ItemEntry<Item> ANTILASER_PLATE = craftingIngredient("antilaser_plate","Anti-laser Plate");

    //Salt Stuff
    public static final ItemEntry<Item> WHITE_HALITE = craftingIngredient("white_halite","White Halite");
    public static final ItemEntry<Item> RED_HALITE = craftingIngredient("red_halite","Red Halite");
    public static final ItemEntry<Item> BLUE_HALITE = craftingIngredient("blue_halite","Blue Halite");
    public static final ItemEntry<Item> RAW_SALT = craftingIngredient("raw_salt","Raw Salt");
    public static final ItemEntry<Item> TABLE_SALT = craftingIngredient("table_salt","Table Salt");
    public static final ItemEntry<Item> CHLORINE_CRYSTAL = craftingIngredient("chlorine_crystal","Chlorine Crystal");

    //Steam Stuff
    public static final ItemEntry<Item> INCOMPLETE_PRESSURIZED_CASING = craftingIngredient("incomplete_pressurized_casing","Incomplete Pressurized Casing");
    public static final ItemEntry<Item> TURBINE_COMPONENTS = craftingIngredient("turbine_components","Turbine Components");


    //Chill Channel Stuff
    public static final ItemEntry<ChannelWrenchItem> ChannelWrench = REGISTRATE.item("channel_wrench",ChannelWrenchItem::new)
            .properties(p -> p.stacksTo(1))
            .lang("Chill Channel Adjuster")
            .register();

    public static final ItemEntry<Item> CHILL_STEEL = craftingIngredient("chill_steel","Chillsteel Ingot");
    public static final ItemEntry<Item> CHILL_STEEL_NUGGET = craftingIngredient("chill_steel_nugget","Chillsteel Nugget");


    public static ItemEntry<Item> craftingIngredient(String id,String lang){
        return REGISTRATE.item(id,Item::new).lang(lang).register();
    }
    public static void register(){

    }
}
