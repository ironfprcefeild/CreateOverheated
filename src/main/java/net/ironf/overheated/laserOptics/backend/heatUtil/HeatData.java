package net.ironf.overheated.laserOptics.backend.heatUtil;

import net.minecraft.nbt.CompoundTag;

//This is the class used to transfer heat data, but also has a static mergeheat function
public class HeatData {
    public int Heat;
    public int SuperHeat;
    public int OverHeat;
    public int Volatility;
    public HeatData(int Heat, int SuperHeat, int OverHeat, int Volatility){
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
        int he = 0;
        int o = 0;
        int s = 0;
        int v = 0;
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

    public int getTotalHeat(){
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
        int detracted = Math.min(amount, this.OverHeat);
        this.SuperHeat = detracted * 4;
        this.OverHeat = this.OverHeat - detracted;
    }

    public void collapseSuperHeat(int amount){
        int detracted = Math.min(amount, this.SuperHeat);
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
        tag.putInt(s +"hdheat",write.Heat);
        tag.putInt(s +"hdsuperheat",write.SuperHeat);
        tag.putInt(s+"hdoverheat",write.OverHeat);
        tag.putInt(s+"hdv",write.Volatility);
    }

    public static HeatData readTag(CompoundTag tag, String s){
        return new HeatData(
                tag.getInt(s+"hdheat"),
                tag.getInt(s+"hdsuperheat"),
                tag.getInt(s+"hdoverheat"),
                tag.getInt(s+"hdv")
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
