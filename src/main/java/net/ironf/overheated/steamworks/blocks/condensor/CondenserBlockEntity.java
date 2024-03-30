package net.ironf.overheated.steamworks.blocks.condensor;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler.condensingHandler;
import static net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler.condensingPresentList;
import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class CondenserBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public CondenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public LazyOptional<IFluidHandler> inHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour inTank;
    public LazyOptional<IFluidHandler> outHandler = LazyOptional.empty();

    public SmartFluidTankBehaviour outTank;

    Integer timer = 3;
    @Override
    public void tick() {
        super.tick();
        timer--;
        if (timer == 0){
            Overheated.LOGGER.info("Updating condenser");
            timer = 3;
            FluidStack inStack = inTank.getPrimaryHandler().getFluid();
            Fluid testedFluid = inStack.getRawFluid();
            if (condensingHandler.containsKey(testedFluid)){
                CondensingPacket recipe = condensingHandler.get(testedFluid);
                FluidStack outputFluid = outTank.getPrimaryHandler().getFluid();
                if (inStack.getAmount() >= recipe.inputAmount && (outputFluid.isEmpty() || outputFluid.getRawFluid() == recipe.output) && 1000 - outputFluid.getAmount() >= recipe.outputAmount){
                    outTank.getPrimaryHandler().setFluid(new FluidStack(recipe.output,outputFluid.getAmount() + recipe.outputAmount));
                    inTank.getPrimaryHandler().setFluid(new FluidStack(recipe.input,inStack.getAmount() - recipe.inputAmount));
                }
            }
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(inTank = SmartFluidTankBehaviour.single(this, 1000));
        behaviours.add(outTank = SmartFluidTankBehaviour.single(this, 1000).forbidInsertion());

    }
    @Override
    public void onLoad() {
        super.onLoad();
        this.inHandler = LazyOptional.of(() -> this.inTank.getPrimaryHandler());
        this.outHandler = LazyOptional.of(() -> this.outTank.getPrimaryHandler());

    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inHandler.invalidate();
        this.outHandler.invalidate();
    }
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
            if (side == facing){
                return inTank.getCapability().cast();
            } else if (side == facing.getOpposite()){
                return outTank.getCapability().cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(addIndent(Component.translatable("coverheated.condenser.in_tank")));
        containedFluidTooltip(tooltip,isPlayerSneaking,inHandler);
        tooltip.add(addIndent(Component.translatable("coverheated.condenser.out_tank")));
        containedFluidTooltip(tooltip,isPlayerSneaking,outHandler);
        return true;
    }
}
