package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.ironf.overheated.Overheated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlastFurnaceTank implements IFluidHandler {
    /// Wild Wacky Fluid Handling



    public final List<FluidStack> fluids = new ArrayList<>();
    public int capacity = 0;
    public int contained = 0;

    public void setCapacity(int newCapacity){
        if (newCapacity > capacity){
            capacity = newCapacity;
        } else {
            //We have to kill some fluids >:)
            int needToDrain = (capacity - newCapacity);
            while (needToDrain > 0){
                int perTank = Math.floorDiv(needToDrain, Math.max(fluids.size(),1));
                for (int i = 0; i < fluids.size() && needToDrain > 0; i++){
                    int drained = Math.min(fluids.get(i).getAmount(),Math.min(perTank,needToDrain));
                    fluids.get(i).shrink(drained);
                    needToDrain -= drained;
                }
            }
            capacity = newCapacity;
        }

    }
    @Override
    public int getTanks() {
        if (contained < capacity){
            return fluids.size() + 1;
        }
        return fluids.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (!(tank >= 0 && tank <= fluids.size())){
            return FluidStack.EMPTY;
        }
        return fluids.get(tank);
    }

    public int getCapacity(){
        return capacity;
    }

    //This method ignores tank
    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return contained < capacity;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for (FluidStack fs : fluids){
            if (fs.isFluidEqual(resource)){
                int filled = Math.min(resource.getAmount(),capacity - contained);
                if (action == FluidAction.EXECUTE){
                    fs.grow(filled);
                    contained += filled;
                }
                return filled;
            }
        }
        int filled = Math.min(resource.getAmount(),capacity - contained);
        if (action == FluidAction.EXECUTE) {
            fluids.add(new FluidStack(resource.getFluid(), filled));
            contained += filled;
        }
        return filled;
    }


    public boolean boolFill(FluidStack resource,FluidAction action){
        return resource.getAmount() == fill(resource,action);
    }
    public boolean boolDrain(FluidIngredient resource,FluidAction action){
        return resource.getRequiredAmount() == drainFluidIng(resource,action);
    }


    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack toReturn = FluidStack.EMPTY;
        FluidStack toRemove = FluidStack.EMPTY;
        for (FluidStack fs : fluids){
            if (fs.isFluidEqual(resource)){
                int drained = Math.min(fs.getAmount(),resource.getAmount());
                if (action.execute()){
                    contained -= drained;
                    fs.shrink(drained);
                    if (fs.getAmount() <= 0){
                        toRemove = fs.copy();
                    }
                }
                toReturn = new FluidStack(resource.getFluid(),drained);
            }
        }
        if (toRemove != FluidStack.EMPTY && action.execute()){
            fluids.remove(toRemove);
        }
        return toReturn;
    }


    //Returns a drained amount
    public @NotNull Integer drainFluidIng(FluidIngredient resource, FluidAction action) {
        int drained = 0;
        FluidStack toRemove = FluidStack.EMPTY;
        for (FluidStack fs : fluids){
            if (resource.test(fs)){
                drained = Math.min(fs.getAmount(),resource.getRequiredAmount());
                if (action.execute()){
                    contained -= drained;
                    fs.shrink(drained);
                    if (fs.getAmount() <= 0){
                        toRemove = fs.copy();
                    }
                }
            }
        }
        if (toRemove != FluidStack.EMPTY && action.execute()){
            fluids.remove(toRemove);
        }
        return drained;
    }

        /// Tag Time!
    //Read Write stuff, this is also simplifier to tinker's implementation
    public void writeTag(CompoundTag tag, String s){
        ListTag list = new ListTag();
        for (FluidStack liquid : fluids) {
            CompoundTag fluidTag = new CompoundTag();
            liquid.writeToNBT(fluidTag);
            list.add(fluidTag);
        }
        tag.put(s+"fluids", list);
        tag.putInt(s+"capacity", capacity);
    }

    public void readTag(CompoundTag tag, String s){
        capacity = tag.getInt(s+"capacity");
        
        ListTag fluidTag = tag.getList(s+"fluids", Tag.TAG_COMPOUND);
        fluids.clear();
        contained = 0;
        for (int i = 0; i < fluidTag.size(); i++) {
            FluidStack toAdd = FluidStack.loadFluidStackFromNBT(fluidTag.getCompound(i));
            if (toAdd.isEmpty()) {continue;}
            fluids.add(toAdd);
            contained += toAdd.getAmount();
        }
    }





    //This method should not be used
    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        Overheated.LOGGER.info("An invalid method has been used on an IBF!");
        return FluidStack.EMPTY;
    }
    //This should not be used
    @Override
    public int getTankCapacity(int tank) {
        return fluids.get(tank).getAmount();
    }
}
