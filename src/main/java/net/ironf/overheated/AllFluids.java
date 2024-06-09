package net.ironf.overheated;

import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.AllSteamFluids;

public class AllFluids {
    public static void register(){
        AllSteamFluids.register();
        AllGasses.register();
    }


}
