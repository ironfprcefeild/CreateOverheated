package net.ironf.overheated.steamworks.blocks.geothermals;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.steamworks.AllSteamFluids.DISTILLED_WATER;
import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class GeothermalInterfaceBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public GeothermalInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    int timer = 75;
    int SteamBuildup = 0;

    String heatingStatus = "none";
    @Override
    public void tick() {
        super.tick();
        if (timer-- <= 0){
            timer = 75;
            heatingStatus = "none";
            BlockState below = level.getBlockState(getBlockPos().below());
            //Above a superheated vent
            boolean superHeated = below == AllBlocks.SUPERHEATED_VENT.getDefaultState();
            //Not above a superheated vent, check to make sure we are at least above a heated one
            if (!superHeated && below != AllBlocks.HEATED_VENT.getDefaultState()) {
                return;
            }
            //Updates heating status,
            heatingStatus = superHeated ? "superheated" : "heated";
            //Build up Steam
            SteamBuildup += 10;
            if (SteamBuildup < 1000){
                return;
            }
            BlockPos above = getBlockPos().above();
            if (tank.getPrimaryHandler().getFluid().getFluid() == DISTILLED_WATER.SOURCE.get()
                    && tank.getPrimaryHandler().getFluidAmount() >= 1000
                    && level.getBlockState(above) == Blocks.AIR.defaultBlockState()){
                tank.getPrimaryHandler().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                SteamBuildup = 0;
                level.setBlock(above, GasMapper.InvGasMap.get(superHeated ? AllSteamFluids.SUPERHEATED_STEAM_MID : AllSteamFluids.HEATED_STEAM_MID).get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        heatingStatus = "none";
        BlockState below = level.getBlockState(getBlockPos().below());
        //Above a superheated vent
        boolean superHeated = below == AllBlocks.SUPERHEATED_VENT.getDefaultState();
        //Not above a superheated vent, check to make sure we are at least above a heated one
        if (!superHeated && below != AllBlocks.HEATED_VENT.getDefaultState())
            return;
        //Updates heating status,
        heatingStatus = superHeated ? "superheated" : "heated";
    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 2000));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());

    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(addIndent(Component.translatable("coverheated.geothermal.heating." + heatingStatus).withStyle(ChatFormatting.RED)));
        tooltip.add(addIndent(Component.translatable("coverheated.geothermal.steam_buildup").append(String.valueOf(SteamBuildup + "/1000")).withStyle(ChatFormatting.WHITE)));
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
        heatingStatus = tag.getString("status");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        tag.putString("status",heatingStatus);
    }
}
