package net.ironf.overheated.cooling;

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

}
