package net.ironf.overheated.laserOptics.backend.heatUtil;

import net.minecraft.nbt.CompoundTag;

//This is the class used to transfer heat data, but also has a static mergeheat function
public class HeatData {
    public float Heat;
    public float SuperHeat;
    public float OverHeat;

    public HeatData(float Heat, float SuperHeat, float OverHeat){
        this.Heat = Heat;
        this.SuperHeat = SuperHeat;
        this.OverHeat = OverHeat;
    }

    public HeatData copyMe(){
        return new HeatData(this.Heat,this.SuperHeat,this.OverHeat);
    }
    public static HeatData empty(){
        return new HeatData(0,0,0);
    }

    public static HeatData mergeHeats(HeatData a, HeatData b){

        return new HeatData(a.Heat + b.Heat, a.SuperHeat + b.SuperHeat,a.OverHeat + b.OverHeat);
    }

    public static HeatData mergeHeats(HeatData[] h){
        float he = 0;
        float o = 0;
        float s = 0;
        float v = 0;
        for (HeatData hd : h){
            he += hd.Heat;
            o += hd.OverHeat;
            s += hd.SuperHeat;
        }
        return new HeatData(he,s,o);
    }

    public float getTotalHeat(){
        return this.Heat + this.SuperHeat * 4 + this.OverHeat * 16;
    }

    //Use methods return an int corresponding to heat level consumed
    //0 : none used, 1 : heat used, 2 : super heat used, 3: overheat used
    public int useHeat(){
        return useHeat(1);
    }
    public int useHeat(float comparison){
        if (this.Heat >= comparison){
            this.Heat--;
            return 1;
        }
        return 0;
    }
    public int useUpToSuperheat(){
        return useUpToSuperheat(1);
    }

    public int useUpToSuperheat(float comparison){
        if (this.SuperHeat >= comparison){
            this.SuperHeat--;
            return 2;
        }
        return useHeat();
    }

    public int useUpToOverHeat(){
        return useUpToOverHeat(1);
    }

    public int useUpToOverHeat(float comparison){
        if (this.OverHeat >= comparison){
            this.OverHeat--;
            return 3;
        }
        return useUpToSuperheat(comparison);
    }



    //Theese methods collapse a heat level into the heat level below.
    public void expandOverHeat() {
        this.SuperHeat = this.OverHeat * 4;
        this.OverHeat = 0;
    }

    public void expandSuperHeat(){
        this.Heat = this.SuperHeat * 4;
        this.SuperHeat = 0;
    }

    public void expandOverHeat(int amount) {
        float detracted = Math.min(amount, this.OverHeat);
        this.SuperHeat = detracted * 4;
        this.OverHeat = this.OverHeat - detracted;
    }

    public void expandSuperHeat(int amount){
        float detracted = Math.min(amount, this.SuperHeat);
        this.Heat = detracted * 4;
        this.SuperHeat = this.SuperHeat - detracted;
    }

    public void expandAllHeat(){
        expandOverHeat();
        expandSuperHeat();
    }

    public void capHeat(float cap){
        float total = getTotalHeat();
        if (total > cap){
            subtractAndExpand(total-cap);
        }
    }

    public void subtractAndExpand(float amount){
        float tracker = amount;
        while (tracker > 0) {
            if (this.OverHeat >= 1 && tracker >= 16) {
                this.OverHeat--;
                tracker = tracker - 16;
            } else if (this.SuperHeat >= 1 && tracker >= 4) {
                this.SuperHeat--;
                tracker = tracker - 4;
            } else if (this.Heat >= 1){
                this.Heat--;
                tracker--;
            } else {
                //We have no lvl1 heat, and less than 4 heat left to remove, so we must try to collapse
                if (this.SuperHeat >= 1){
                    this.expandSuperHeat(1);
                } else if (this.OverHeat >= 1){
                    this.expandOverHeat(1);
                }
            }
        }
    }

    //Gets the amount of heat of a level greater than or equal to the specified level, not considering collapsing
    public float getHeatOfLevel(int heatLevel){
        return  (heatLevel == 1 ? this.Heat + this.SuperHeat + this.OverHeat : (heatLevel == 2 ? this.SuperHeat + this.OverHeat : this.OverHeat));
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

    //Read Write stuff
    public void writeTag(CompoundTag tag, String s){
        HeatData.writeTag(tag,this,s);
    }
    public static void writeTag(CompoundTag tag, HeatData write, String s){
        tag.putFloat(s +"hdheat",write.Heat);
        tag.putFloat(s +"hdsuperheat",write.SuperHeat);
        tag.putFloat(s +"hdoverheat",write.OverHeat);

    }

    public static HeatData readTag(CompoundTag tag, String s){
        return new HeatData(
                tag.getFloat(s+"hdheat"),
                tag.getFloat(s+"hdsuperheat"),
                tag.getFloat(s+"hdoverheat")
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
