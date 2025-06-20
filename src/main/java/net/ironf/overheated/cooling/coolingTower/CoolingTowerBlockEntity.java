package net.ironf.overheated.cooling.coolingTower;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.cooling.IAirCurrentReader;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.gasses.GasMapper;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class CoolingTowerBlockEntity extends SmartBlockEntity implements ICoolingBlockEntity, IAirCurrentReader, IHaveGoggleInformation {
    public CoolingTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    ////Doing Stuff
    int fanTimer = 5;
    int tickTimer = 75;
    int vaporCounter = 5;
    float sunken = 0;
    float recentCoolingUnits = 0f;
    @Override
    public void tick() {
        super.tick();
        if (fanTimer <= 0){
            sunken = 0;
        } else {
            fanTimer--;
        }
        if (tickTimer-- < 1){
            IFluidTank tank = getTank();
            if (tank != null && tank.getFluidAmount() >= 2
                    && AllSteamFluids.getSteamPressure(tank.getFluid().getFluid()) >= 1
                    && level.getBlockState(getBlockPos().above()) == Blocks.AIR.defaultBlockState()){
                tank.drain(2, IFluidHandler.FluidAction.EXECUTE);
                recentCoolingUnits = 12800 * sunken;
                if (vaporCounter-- < 1) {
                    vaporCounter = 5;
                    level.setBlockAndUpdate(getBlockPos().above(), GasMapper.InvGasMap.get(AllGasses.water_vapor).get().defaultBlockState());
                }
            } else {
                recentCoolingUnits = 0;
            }
            tickTimer = 75;
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        fanTimer = tag.getInt("fantimer");
        tickTimer = tag.getInt("ticktimer");
        vaporCounter = tag.getInt("vaporcounter");
        sunken = tag.getFloat("sunken");
        recentCoolingUnits = tag.getFloat("recentcoolingunits");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("fantimer",fanTimer);
        tag.putInt("ticktimer",tickTimer);
        tag.putInt("vaporcounter",vaporCounter);
        tag.putFloat("sunken",sunken);
        tag.putFloat("recentcoolingunits",recentCoolingUnits);
    }

    public IFluidTank getTank(){
        return (level.getBlockEntity(getBlockPos().relative(Direction.DOWN)) instanceof FluidTankBlockEntity fbe) ? fbe.getControllerBE().getTankInventory() : null;
    }

    //Air Current Reading
    @Override
    public void update(float strength, Direction incoming) {
        sunken = Math.abs(strength)/256;
        fanTimer = 5;
    }

    //Cooling
    @Override
    public CoolingData getGeneratedCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in) {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (facing.getOpposite() == in){
            return new CoolingData(recentCoolingUnits,-20f);
        } else {
            return CoolingData.empty();
        }

    }

    //Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (recentCoolingUnits == 0){
            tooltip.add(GoggleHelper.addIndent((Component.translatable("coverheated.cooling_tower.invalid"))));
            tooltip.add(GoggleHelper.addIndent((Component.translatable("coverheated.cooling_tower.invalid_2"))));
            tooltip.add(GoggleHelper.addIndent((Component.translatable("coverheated.cooling_tower.invalid_3"))));

            return true;
        }

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.cooling_tower.cooling").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(recentCoolingUnits)).withStyle(ChatFormatting.AQUA),1));

        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.cooling_tower.airflow").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(sunken)).withStyle(ChatFormatting.AQUA),1));

        return true;
    }
}
