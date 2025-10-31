package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace;

import com.google.gson.JsonObject;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;

public class BlastFurnaceStatus {

    public HeatData currentHeating;

    public int PressureLevel;
    public int SteamAmount;

    //This bit is probably going to change as batteries are worked on but I think it makes sense to put here now
    public int[] batteryCharges;

    public BlastFurnaceStatus(HeatData heating, int pressureLevel, int steamAmount, int[] charges){
        this.currentHeating = heating;
        this.PressureLevel = pressureLevel;
        this.SteamAmount = steamAmount;
        this.batteryCharges = charges;
    }

    public BlastFurnaceStatus(JsonObject j){

        //Heat
        currentHeating = new HeatData(0,0,0);
        int heatRating = j.has("overheat") ? 3 : (j.has("superheat") ? 2 : (j.has("heat") ? 1 : 0));
        if (heatRating > 0) {
            currentHeating.modifyHeatOfLevel(heatRating, GsonHelper.getAsFloat(j, HeatData.InvNameMap.get(heatRating)));
        }
        //Charge
        batteryCharges = new int[]{0, 0, 0, 0};
        if (j.has("voltage_level") || j.has("charge")) {
            int voltage_level = GsonHelper.getAsInt(j, "voltage_level");
            int voltage_amount = GsonHelper.getAsInt(j,"charge");
            batteryCharges[voltage_level] = voltage_amount;
        }
        //Steam
        SteamAmount = 0;
        PressureLevel = 0;
        if (j.has("steam_pressure") || j.has("steam_amount")){
            SteamAmount = GsonHelper.getAsInt(j,"steam_amount");
            PressureLevel = GsonHelper.getAsInt(j,"steam_pressure");
        }

    }

    //Tests the parameter with this object as the requirement
    //This assumes that the requirment (this) has all 0s in its charge array except for 1 spot.
    public boolean compareWith(BlastFurnaceStatus tested){
        if (HeatData.compare(currentHeating,tested.currentHeating)
            && tested.PressureLevel >= PressureLevel
            && tested.SteamAmount >= SteamAmount){
            for (int i = 3; i >= 0 ; i--) {
                if (!(tested.batteryCharges[i] >= batteryCharges[i])){
                    return false;
                }
            }
            return true;
        }
        return false;
    }



    //Read Write stuff
    public void writeTag(CompoundTag tag, String s){
        writeTag(tag,this,s);
    }
    public static void writeTag(CompoundTag tag, BlastFurnaceStatus write, String s){
        tag.putInt(s +"ibfssteamamount",write.SteamAmount);
        tag.putInt(s +"ibfssteampressure",write.PressureLevel);
        write.currentHeating.writeTag(tag,s + "ibfsheating");
        tag.putIntArray(s + "ibfscharges",write.batteryCharges);
    }

    public static BlastFurnaceStatus readTag(CompoundTag tag, String s){
        return new BlastFurnaceStatus(
                HeatData.readTag(tag,s + "ibfsheating"),
                tag.getInt(s+"ibfssteampressure"),
                tag.getInt(s+"ibfssteamamount"),
                tag.getIntArray(s + "ibfscharges")
        );
    }
}
