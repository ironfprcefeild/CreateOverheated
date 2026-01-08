package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace;

import com.google.gson.JsonObject;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BlastFurnaceStatus {


    public int airHeat;
    public int airAmount;


    //This bit is probably going to change as batteries are worked on but I think it makes sense to put here now
    public int[] batteryCharges;

    public BlastFurnaceStatus(int heating, int airAmount, int[] charges){
        this.airHeat = heating;
        this.airAmount = airAmount;
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
        //Air
        airAmount = 0;
        airHeat = 0;
        if (j.has("air_amount") || j.has("air_heating")){
            airAmount = GsonHelper.getAsInt(j,"air_amount");
            airHeat = GsonHelper.getAsInt(j,"air_heating");
        }

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
        j.addProperty("air_amount", airAmount);
        j.addProperty("air_heating", airHeat);
        return j;
    }

    public BlastFurnaceStatus changeSteamAmount(int amount){
        this.airAmount = amount;
        return this;
    }

    //Tests the parameter with this object as the requirement
    //This assumes that the requirement (this) has all 0s in its charge array except for 1 spot.
    //When simulate is passed as true, this will attempt to drain air and batteries even if it cannot
    public boolean compareWith(BlastFurnaceStatus tested, BlastFurnaceControllerBlockEntity actUpon, boolean simulate){
        if (tested.airHeat >= airHeat
            && tested.drainAir(airAmount,simulate,actUpon)){
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
    public boolean drainAir(int amount, boolean simulate, BlastFurnaceControllerBlockEntity IBF){
        if (amount >= airAmount){
            //We have enough steam!
            if (!simulate){
                airAmount -= amount;
                IBF.AirTank.getPrimaryHandler().drain(amount, IFluidHandler.FluidAction.EXECUTE);
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
        tag.putInt(s +"ibfsairamount",write.airAmount);
        tag.putInt(s + "ibfsairheat",write.airHeat);

        tag.putIntArray(s + "ibfscharges",write.batteryCharges);
    }

    public static BlastFurnaceStatus readTag(CompoundTag tag, String s){
        return new BlastFurnaceStatus(
                tag.getInt(s+"ibfsairheat"),
                tag.getInt(s+"ibfsairamount"),
                tag.getIntArray(s + "ibfscharges")
        );
    }

    /*
        1. int  - Air heat
        2. int  - Air amount
        3. int  - lv Charges
        4. int  - mv Charges
        5. int  - hv Charges
        6. int  - iv Charges
     */
    public static BlastFurnaceStatus readFromBuffer(FriendlyByteBuf buf){
        return new BlastFurnaceStatus(buf.readInt(),buf.readInt(),
                new int[]{buf.readInt(),buf.readInt(),buf.readInt(),buf.readInt()});
    }
    public void writeToBuffer(FriendlyByteBuf buf){
        buf.writeInt(airHeat); 
        buf.writeInt(airAmount);
        buf.writeInt(batteryCharges[0]);
        buf.writeInt(batteryCharges[1]);
        buf.writeInt(batteryCharges[2]);
        buf.writeInt(batteryCharges[3]);
    }

    public static BlastFurnaceStatus empty(){
        return new BlastFurnaceStatus(0,0,
                new int[]{0,0,0,0});
    }
}
