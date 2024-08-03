package net.ironf.overheated.steamworks.blocks.condensor;

import net.minecraftforge.fluids.FluidStack;

public class CondensingOutputBundle {
    FluidStack output;
    float minTemp;
    float addTemp;
    public CondensingOutputBundle(FluidStack o, float m, float a){this.output = o; this.minTemp = m; this.addTemp = a;}
}
