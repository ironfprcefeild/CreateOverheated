package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.fluidDuct;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.BlastFurnaceServantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.steamworks.AllSteamFluids.isSteam;
import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class FluidDuctBlockEntity extends BlastFurnaceServantBlockEntity implements IWrenchable {
    public FluidDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    //TODO remove unneeded nonsense and let this extract steam/oxygen. We could also add a steam vent that specifically gets steam and oxygen
    //Filter Slot


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }



    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && controllerPos != null) {
            if (level.getBlockEntity(controllerPos) instanceof BlastFurnaceControllerBlockEntity ibf) {
                ibf.setChanged();
                return mode ? ibf.AirTank.getCapability().cast() : ibf.MainTank.getCapability().cast();
            }
        }
        return super.getCapability(cap, side);
    }

    //false = main tank, true = air tank,
    public boolean mode = false;

    public void wrench() {
        mode = !mode;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(addIndent(Component.translatable("coverheated.ibf.fluid_duct.mode." +
                (mode ? "air_tank" : "main_tank"))));
        return true;
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("mode",mode);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        mode = tag.getBoolean("mode");
    }
}
