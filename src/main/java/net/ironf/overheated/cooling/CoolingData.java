package net.ironf.overheated.cooling;

import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.nbt.CompoundTag;

//Includes information about coolant level and min temperature
public class CoolingData {
    public float coolingUnits;
    public float minTemp;
    public CoolingData(float CoolingUnits, float MinTemp){
        this.coolingUnits = CoolingUnits;
        this.minTemp = MinTemp;
    }
    public void add(CoolingData toAdd){
        coolingUnits += toAdd.coolingUnits;
        minTemp = Math.min(toAdd.minTemp,minTemp);
    }

    static CoolingData add(CoolingData a, CoolingData b){
        return new CoolingData(a.coolingUnits + b.coolingUnits, Math.min(a.minTemp, b.minTemp));
    }

    public static CoolingData empty(){
        return new CoolingData(0,10000);
    }

    public void writeTag(CompoundTag tag, String s){
        writeTag(tag,this,s);
    }
    public static void writeTag(CompoundTag tag, CoolingData write, String s){
        tag.putFloat(s +"cdunits",write.coolingUnits);
        tag.putFloat(s +"cdminimum",write.minTemp);

    }

    public static CoolingData readTag(CompoundTag tag, String s){
        return new CoolingData(
                tag.getFloat(s+"cdunits"),
                tag.getFloat(s+"cdminimum")
        );
    }

}
