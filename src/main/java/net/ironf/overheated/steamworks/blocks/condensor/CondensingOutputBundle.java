package net.ironf.overheated.steamworks.blocks.condensor;

import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraftforge.fluids.FluidStack;

public class CondensingOutputBundle {
    FluidStack output;
    float minTemp;
    float addTemp;
    HeatData outputHeat;
    public CondensingOutputBundle(FluidStack o, float m, float a, HeatData outputHeat){
        this.output = o;
        this.minTemp = m;
        this.addTemp = a;
        this.outputHeat = outputHeat;
    }
}
