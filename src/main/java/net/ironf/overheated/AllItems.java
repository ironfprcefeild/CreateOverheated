package net.ironf.overheated;

import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;

public class AllItems {
    static {
        Overheated.REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.OVERHEATED_TAB);
    }

    public static void register(){

    }
}
