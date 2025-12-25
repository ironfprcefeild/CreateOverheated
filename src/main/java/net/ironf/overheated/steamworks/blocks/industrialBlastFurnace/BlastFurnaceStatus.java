package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace;

import com.google.gson.JsonObject;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Objects;

public class BlastFurnaceStatus {


    public int steamHeat;
    public int PressureLevel;
    public int SteamAmount;

    public int OxygenAmount;

    //This bit is probably going to change as batteries are worked on but I think it makes sense to put here now
    public int[] batteryCharges;

    public BlastFurnaceStatus(int heating, int pressureLevel, int steamAmount, int OxygenAmount, int[] charges){
        this.steamHeat = heating;
        this.PressureLevel = pressureLevel;
        this.SteamAmount = steamAmount;
        this.OxygenAmount = OxygenAmount;
        this.batteryCharges = charges;
    }

    public BlastFurnaceStatus(JsonObject j){

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
        steamHeat = 0;
        if (j.has("steam_pressure") || j.has("steam_amount") || j.has("steam_heating")){
            SteamAmount = GsonHelper.getAsInt(j,"steam_amount");
            PressureLevel = GsonHelper.getAsInt(j,"steam_pressure");
            steamHeat = GsonHelper.getAsInt(j,"steam_heating");
        }

        //This should not be used as a recipe requirement
        OxygenAmount = 0;

    }

    public JsonObject toJson(){
        JsonObject j = new JsonObject();
        for (int i = 0; i < batteryCharges.length; i++) {
            if (batteryCharges[i] != 0){
                j.addProperty("voltage_level",i);
                j.addProperty("charge",batteryCharges[i]);
                break;
            }
        }
        j.addProperty("steam_amount",SteamAmount);
        j.addProperty("steam_heating",steamHeat);
        j.addProperty("steam_pressure",PressureLevel);
        return j;
    }

    public BlastFurnaceStatus changeSteamAmount(int amount){
        this.SteamAmount = amount;
        return this;
    }

    //Tests the parameter with this object as the requirement
    //This assumes that the requirement (this) has all 0s in its charge array except for 1 spot.
    //When simulate is passed as true, this will attempt to drain steam and batteries even if it cannot
    public boolean compareWith(BlastFurnaceStatus tested, BlastFurnaceControllerBlockEntity actUpon, boolean simulate){
        if (tested.steamHeat >= steamHeat
            && tested.PressureLevel >= PressureLevel
            && tested.drainSteam(SteamAmount,simulate,actUpon)){
            for (int i = 3; i >= 0 ; i--) {
                if (tested.batteryCharges[i] < batteryCharges[i]){
                    return false;
                } else if (!simulate){
                    tested.batteryCharges[i] -= batteryCharges[i];
                }
            }
            return true;
        }
        return false;
    }

    public boolean compareWith(BlastFurnaceControllerBlockEntity tested, boolean simulate){
       return compareWith(tested.BFData,tested,simulate);
    }

    //Considers Oxygen as a supplement
    public boolean drainSteam(int amount, boolean simulate, BlastFurnaceControllerBlockEntity IBF){

        int injectedMB = Math.min(amount,Math.min(Math.floorDiv(OxygenAmount,3),SteamAmount) * 4);

        int newOAmount = OxygenAmount - Math.floorDiv((3*injectedMB), 4);
        int newSAmount = SteamAmount - Math.floorDiv(injectedMB,4);

        amount -= injectedMB;

        if (amount > 0){
            newSAmount -= amount;
        }

        if (newSAmount >= 0){
            //We have enough steam!
            if (!simulate){
                OxygenAmount = newOAmount;
                SteamAmount = newSAmount;
                IBF.SteamTank.getPrimaryHandler().setFluid(new FluidStack(IBF.SteamTank.getPrimaryHandler().getFluid(),newSAmount));
                IBF.OxygenTank.getPrimaryHandler().setFluid(new FluidStack(IBF.OxygenTank.getPrimaryHandler().getFluid(),newOAmount));
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
        tag.putInt(s + "ibfsteamheat",write.steamHeat);
        tag.putInt(s + "ibfoxygenamout",write.OxygenAmount);
        tag.putIntArray(s + "ibfscharges",write.batteryCharges);
    }

    public static BlastFurnaceStatus readTag(CompoundTag tag, String s){
        return new BlastFurnaceStatus(
                tag.getInt(s+"ibfsteamheat"),
                tag.getInt(s+"ibfssteampressure"),
                tag.getInt(s+"ibfssteamamount"),
                tag.getInt(s+"ibfoxygenamount"),
                tag.getIntArray(s + "ibfscharges")
        );
    }
    
    /*
        1. int  - Steam heat
        2. int  - Steam pressure
        3. int  - Steam Amount
        4. int  - lv Charges
        5. int  - mv Charges
        6. int  - hv Charges
        7. int  - iv Charges
     */
    public static BlastFurnaceStatus readFromBuffer(FriendlyByteBuf buf){
        return new BlastFurnaceStatus(buf.readInt(),buf.readInt(),buf.readInt(),0,
                new int[]{buf.readInt(),buf.readInt(),buf.readInt(),buf.readInt()});
    }
    public void writeToBuffer(FriendlyByteBuf buf){
        buf.writeInt(steamHeat); 
        buf.writeInt(PressureLevel);
        buf.writeInt(SteamAmount);
        buf.writeInt(batteryCharges[0]);
        buf.writeInt(batteryCharges[1]);
        buf.writeInt(batteryCharges[2]);
        buf.writeInt(batteryCharges[3]);
    }

    public static BlastFurnaceStatus empty(){
        return new BlastFurnaceStatus(0,0,0,0,
                new int[]{0,0,0,0});
    }
}
