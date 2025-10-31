package net.ironf.overheated.batteries;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.minecraft.world.item.Item;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBatteryItems {


    //Batteries
    public static void register(){

    }

    static {
        Overheated.LOGGER.info("Registering Batteries in AllBatteryItems");
        Overheated.LOGGER.info("Preparing Battery Array");
        Overheated.REGISTRATE.setCreativeTab(AllCreativeModeTabs.OVERHEATED_TAB);
    }

    public static final ItemEntry<Item>[] BATTERIES = new ItemEntry[]
            {batteryEntry("Empty"),
            batteryEntry("LV"),
            batteryEntry("MV"),
            batteryEntry("HV"),
            batteryEntry("IV")};


    public static ItemEntry<Item> batteryEntry(String voltagesString){
        return REGISTRATE.item(
                        voltagesString.toLowerCase().replaceAll(" ","_")+"_battery",Item::new)
                .lang(voltagesString + " Battery")
                .register();
    }

    public static ItemEntry<Item> getBattery(int voltage){
        return BATTERIES[voltage];
    }

    public static Item getBatteryItem(int voltage){
        return getBattery(voltage).get();
    }
}
