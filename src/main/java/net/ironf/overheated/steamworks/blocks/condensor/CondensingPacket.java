package net.ironf.overheated.steamworks.blocks.condensor;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CondensingPacket {
    Fluid input;
    Fluid output;
    int inputAmount;
    int outputAmount;
    public CondensingPacket(FluidStack inputStack, FluidStack outputStack){
            this.input = inputStack.getFluid();
            this.output = outputStack.getFluid();
            this.inputAmount = inputStack.getAmount();
            this.outputAmount = outputStack.getAmount();
    }
    public CondensingPacket(Fluid input, int inputAmount, Fluid output, int outputAmount){
        this.input = input;
        this.output = output;
        this.inputAmount = inputAmount;
        this.outputAmount = outputAmount;
    }
}
