package net.ironf.overheated;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.minecraft.world.item.Item;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllItems {
    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    //Industrial Sheet Stuff
    public static final ItemEntry<Item> INDUSTRIAL_SHEET = craftingIngredient("industrial_sheet");
    public static final ItemEntry<Item> INCOMPLETE_INDUSTRIAL_SHEET = craftingIngredient("incomplete_industrial_sheet");

    //Nihilite Stuff
    public static final ItemEntry<Item> NIHILTE_INGOT = craftingIngredient("nihilite_ingot");
    public static final ItemEntry<Item> NIHILTE_NUGGET = craftingIngredient("nihilite_nugget");
    public static final ItemEntry<Item> NIHILTE_GLOBULE = craftingIngredient("nihilite_globule");
    public static final ItemEntry<Item> CRUSHED_NIHILITE = craftingIngredient("crushed_nihilite");


    //Blazesteel and Laser stuff
    public static final ItemEntry<Item> BLAZESTEEL_INGOT = craftingIngredient("blazesteel_ingot");
    public static final ItemEntry<Item> BLAZESTEEL_NUGGET = craftingIngredient("blazesteel_nugget");
    public static final ItemEntry<Item> BLAZEGLASS_FIXTURE = craftingIngredient("blazeglass_fixture");
    public static final ItemEntry<Item> ANTILASER_PLATE = craftingIngredient("antilaser_plate");

    //Salt Stuff
    public static final ItemEntry<Item> WHITE_HALITE = craftingIngredient("white_halite");
    public static final ItemEntry<Item> RED_HALITE = craftingIngredient("red_halite");
    public static final ItemEntry<Item> BLUE_HALITE = craftingIngredient("blue_halite");
    public static final ItemEntry<Item> RAW_SALT = craftingIngredient("raw_salt");
    public static final ItemEntry<Item> TABLE_SALT = craftingIngredient("table_salt");
    public static final ItemEntry<Item> CHLORINE_CRYSTAL = craftingIngredient("chlorine_crystal");

    //Steam Stuff
    public static final ItemEntry<Item> INCOMPLETE_PRESSURIZED_CASING = craftingIngredient("incomplete_pressurized_casing");
    public static final ItemEntry<Item> TURBINE_COMPONENTS = craftingIngredient("turbine_components");


    public static ItemEntry<Item> craftingIngredient(String name){
        return REGISTRATE.item(name,Item::new).register();
    }
    public static void register(){

    }
}
