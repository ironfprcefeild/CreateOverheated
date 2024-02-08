package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DiodeBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IWrenchable {
    public DiodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    //Doing stuff
    public boolean hasClearance = false;

    //This list holds the points that we turn in the laser beam, which is enough to infer rendering in the render
    List<Long> renderTurns = new ArrayList<>();

    //Laser is updated every tick
    @Override
    public void tick() {
        super.tick();

    }

    //This method looks for all diodes within 2 blocks, the area in which they could disrupt diodes clearance. It also invalidates those diodes
    public void testForClearance(){
        for (int x = -2; x != 3; x++){
            for (int y = -2; y != 3; y++){
                for (int z = -2; z != 3; z++) {
                    if (x == 0 && y == 0) {
                        continue;
                    }
                    BlockEntity test = level.getBlockEntity(getBlockPos().offset(x,y,z));
                    if (test != null && test.getType() == AllBlockEntities.DIODE.get()){
                        this.setNoClearance();
                        ((DiodeBlockEntity) test).setNoClearance();
                    }
                }
            }
        }
    }

    public void setNoClearance(){
        hasClearance = false;
    }

    public void markTurnToRender(BlockPos toMark){
        renderTurns.add(toMark.asLong());
    }

    @Override
    public void initialize() {
        super.initialize();
        testForClearance();
    }

    //Data Writing

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        hasClearance = tag.getBoolean("has_clearance");
        long[] rawTurns = (tag.getLongArray("render_turns"));
        for (long l : rawTurns){
            renderTurns.add(l);
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("has_clearance",hasClearance);
        tag.putLongArray("render_turns",renderTurns);
    }

    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (!hasClearance){
            tooltip.add(Component.translatable("coverheated.diode.needs_clearance"));
        }
        return true;
    }


    //Wrenchable

    //Wrenching the diode will retest for clearance
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!hasClearance){
            testForClearance();
        }
        return InteractionResult.SUCCESS;
    }


    //Fluids
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 6000));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyFluidHandler.invalidate();
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
    public void setFluid(FluidStack stack) {
        this.tank.getPrimaryHandler().setFluid(stack);
    }
    public FluidStack getFluidStack() {
        return this.tank.getPrimaryHandler().getFluid();
    }

}
