package net.ironf.overheated;

import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.steamFluids.AllSteamFluids;

public class AllFluids {
    public static void register(){
        loadClass();
        AllSteamFluids.register();
        AllGasses.register();
    }

    public static void loadClass(){

    }
}
