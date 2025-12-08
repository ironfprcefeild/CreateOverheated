package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.ironf.overheated.Overheated;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.antlr.v4.codegen.model.Sync;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlastFurnaceTank extends BlockEntityBehaviour implements IFluidHandler {

    /// Capabilities
   public final SmartBlockEntity blockEntity;
    protected LazyOptional<? extends IFluidHandler> capability;
    public LazyOptional<? extends IFluidHandler> getCapability() {
        return capability;
    }

    public BlastFurnaceTank(SmartBlockEntity be){
        super(be);
        capability = LazyOptional.of(() -> this);
        blockEntity = be;
        queueUpdate();
    }

    public void updateFluids() {
        Overheated.LOGGER.info("Updating Fluids?");
        blockEntity.sendData();
        blockEntity.setChanged();
    }

    /// Syncing
    boolean updateQueued = false;
    public void queueUpdate(){
        updateQueued = true;
        Overheated.LOGGER.info("Updated Queued");
    }
    public void tick(){
        if (updateQueued){
            Overheated.LOGGER.info("Tying to update");
            updateFluids();
            updateQueued = false;
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;
        queueUpdate();
    }

    @Override
    public void unload() {
        super.unload();
        capability.invalidate();
    }

    /// Behavior
    public static final BehaviourType<BlastFurnaceTank> TYPE = new BehaviourType<>("ibftank");

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public static BlastFurnaceTank create(SmartBlockEntity be){
        return new BlastFurnaceTank(be);
    }


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
        if (!(tank >= 0 && tank < fluids.size())){
            return FluidStack.EMPTY;
        }
        return fluids.get(tank);
    }



    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    //This method ignores tank
    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return contained < capacity;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        queueUpdate();
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


    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        queueUpdate();
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
        queueUpdate();
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

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        queueUpdate();
        if (contained > 0 && !fluids.isEmpty()){
            int drained = Math.min(fluids.get(0).getAmount(),maxDrain);
            Fluid type = fluids.get(0).getFluid();
            if (action.execute()) {
                fluids.get(0).shrink(drained);
                if (fluids.get(0).isEmpty()){
                    fluids.remove(0);
                }
            }
            return new FluidStack(type,drained);

        } else {
            return FluidStack.EMPTY;
        }
    }


    /// Tag Time!
    @Override
    public void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        String s = getType().getName();
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
        //We don't write if an update is queued, because whenever we read we queue an update
        //This also replaces using a sync timer like some other mods would
        queueUpdate();

    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        String s = getType().getName();
        ListTag list = new ListTag();
        for (FluidStack liquid : fluids) {
            CompoundTag fluidTag = new CompoundTag();
            liquid.writeToNBT(fluidTag);
            list.add(fluidTag);
        }
        tag.put(s+"fluids", list);
        tag.putInt(s+"capacity", capacity);

    }

    /// Goggles
    public boolean addToGoggleTooltip(List<Component> tooltip){
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        CreateLang.translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);

        int contained = 0;
        for (FluidStack fluidStack : fluids) {
            if (fluidStack.isEmpty())
                continue;
            contained += fluidStack.getAmount();
            CreateLang.fluidName(fluidStack)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            CreateLang.builder()
                    .add(CreateLang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .forGoggles(tooltip, 1);
        }

        CreateLang.translate("coverheated.gui.total_capacity")
                .add(CreateLang.number(contained)
                        .add(mb)
                        .style(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(CreateLang.number(capacity)
                        .add(mb)
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }




}
