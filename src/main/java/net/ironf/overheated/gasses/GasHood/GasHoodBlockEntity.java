package net.ironf.overheated.gasses.GasHood;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.lang.ref.WeakReference;
import java.util.List;

import static net.ironf.overheated.gasses.GasMapper.GasMap;
import static net.ironf.overheated.gasses.GasMapper.RawGasMap;

public class GasHoodBlockEntity extends SmartBlockEntity {
    public GasHoodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public FluidTankBlockEntity getTank() {
        BlockEntity be = level.getBlockEntity(worldPosition.relative(GasHoodBlock.getAttachedDirection(getBlockState()).getOpposite()));
        if (be instanceof FluidTankBlockEntity){
            return ((FluidTankBlockEntity) be).getControllerBE();
        } else {
            return null;
        }
    }

    public int timer = 10;

    @Override
    public void tick() {
        super.tick();
        if (timer-- <= 0){
            timer = 8;
            BlockPos mypos = getBlockPos();
            Direction faced = GasHoodBlock.getAttachedDirection(getBlockState());
            BlockState testedState = level.getBlockState(mypos.relative(faced));
            if (testedState != Blocks.AIR.defaultBlockState() && RawGasMap.containsKey(testedState)){
                BlockEntity be = level.getBlockEntity(mypos.relative(faced.getOpposite()));
                if (!(be instanceof FluidTankBlockEntity)) {
                    return;
                }
                FluidTankBlockEntity tank = ((FluidTankBlockEntity) be).getControllerBE();
                ForgeFlowingFluid.Flowing gas = RawGasMap.get(testedState).get();
                if (tank.getTankInventory().fill(new FluidStack(gas,1000), IFluidHandler.FluidAction.SIMULATE) != 1000){
                    return;
                }
                tank.getTankInventory().fill(new FluidStack(gas,1000), IFluidHandler.FluidAction.EXECUTE);
                level.setBlockAndUpdate(mypos.relative(faced), Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
    }
}
