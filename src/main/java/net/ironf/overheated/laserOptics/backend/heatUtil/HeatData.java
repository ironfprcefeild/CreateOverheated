package net.ironf.overheated.laserOptics.backend.heatUtil;

import net.minecraft.nbt.CompoundTag;

//This is the class used to transfer heat data, but also has a static mergeheat function
public class HeatData {
    public float Heat;
    public float SuperHeat;
    public float OverHeat;
    public float Volatility;
    public HeatData(float Heat, float SuperHeat, float OverHeat, float Volatility){
        this.Heat = Heat;
        this.SuperHeat = SuperHeat;
        this.OverHeat = OverHeat;
        this.Volatility = Volatility;
    }

    public static HeatData empty(){
        return new HeatData(0,0,0,0);
    }

    public static HeatData mergeHeats(HeatData a, HeatData b){

        return new HeatData(a.Heat + b.Heat, a.SuperHeat + b.SuperHeat,a.OverHeat + b.OverHeat,
                (a.Volatility + b.Volatility) / ((a.Volatility == 0 || b.Volatility == 0) ? 1 : 2));
    }

    public static HeatData mergeHeats(HeatData[] h){
        float he = 0;
        float o = 0;
        float s = 0;
        float v = 0;
        int ignoreInMerge = 0;
        for (HeatData hd : h){
            he += hd.Heat;
            o += hd.OverHeat;
            s += hd.SuperHeat;
            if (hd.Volatility == 0){
                ignoreInMerge++;
            }
            v += hd.Volatility;
        }
        return new HeatData(he,s,o,(v / (h.length - ignoreInMerge + 1)));
    }

    public float getTotalHeat(){
        return this.Heat + this.SuperHeat * 4 + this.OverHeat * 16;
    }

    //Use methods return an int corresponding to heat level consumed
    //0 : none used, 1 : heat used, 2 : super heat used, 3: overheat used
    public int useHeat(){
        if (this.Heat > 0){
            this.Heat--;
            return 1;
        }
        return 0;
    }
    public int useUpToSuperheat(){
        if (this.SuperHeat > 0){
            this.SuperHeat--;
            return 2;
        }
        return useHeat();
    }

    public int useUpToOverHeat(){
        if (this.OverHeat > 0){
            this.OverHeat--;
            return 3;
        }
        return useUpToSuperheat();
    }
    //Theese methods collapse a heat level into the heat level below.
    public void collapseOverHeat() {
        this.SuperHeat = this.OverHeat * 4;
        this.OverHeat = 0;
    }

    public void collapseSuperHeat(){
        this.Heat = this.SuperHeat * 4;
        this.SuperHeat = 0;
    }

    public void collapseOverHeat(int amount) {
        float detracted = Math.min(amount, this.OverHeat);
        this.SuperHeat = detracted * 4;
        this.OverHeat = this.OverHeat - detracted;
    }

    public void collapseSuperHeat(int amount){
        float detracted = Math.min(amount, this.SuperHeat);
        this.Heat = detracted * 4;
        this.SuperHeat = this.SuperHeat - detracted;
    }

    public void collapseAllHeat(){
        collapseOverHeat();
        collapseSuperHeat();
    }
    //Combines Heat, moving them up levels

    public void combineHeat(){
        while (this.Heat > 3){
            this.SuperHeat++;
            this.Heat = this.Heat - 4;
        }
    }
    public void combineSuperHeat(){
        while (this.SuperHeat > 3){
            this.OverHeat++;
            this.SuperHeat = this.SuperHeat - 4;
        }
    }
    public void combineAllHeat(){
        combineHeat();
        combineSuperHeat();
    }

    //Read Wrtie stuff
    public static void writeTag(CompoundTag tag, HeatData write, String s){
        tag.putFloat(s +"hdheat",write.Heat);
        tag.putFloat(s +"hdsuperheat",write.SuperHeat);
        tag.putFloat(s+"hdoverheat",write.OverHeat);
        tag.putFloat(s+"hdv",write.Volatility);
    }

    public static HeatData readTag(CompoundTag tag, String s){
        return new HeatData(
                tag.getFloat(s+"hdheat"),
                tag.getFloat(s+"hdsuperheat"),
                tag.getFloat(s+"hdoverheat"),
                tag.getFloat(s+"hdv")
        );
    }



    public static void writeHeadDataArray(CompoundTag tag, HeatData[] write, String s){
        int i = 0;
        tag.putInt(s+"hdarraylength",write.length);
        for (HeatData hd : write){
            writeTag(tag,hd,i+s);
        }
    }

    public static HeatData[] readHeatDataArray(CompoundTag tag, String s){
        HeatData[] toReturn = new HeatData[tag.getInt(s+"hdarraylength")];
        for(int i = 0; i != toReturn.length; i++){
            toReturn[i] = readTag(tag,i+s);
        }
        return toReturn;
    }

}
