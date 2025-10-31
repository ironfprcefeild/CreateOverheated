package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class DiodeBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IWrenchable,ILaserEmitter {
    public DiodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    //Doing stuff
    public boolean hasClearance = false;
    boolean activeInefficiency = false;
    boolean heatToLow = false;
    boolean noCoolant = false;
    int coolantConsumptionTicks = 256;
    double breakingCounter = 0;
    LaserSegment LS;

    int timer = 5;
    HeatData recentHeat = HeatData.empty();


    //Laser is updated every tick
    @Override
    public void tick() {
        super.tick();
        if (timer-- <= 0){
            fireLaser();
            timer = 5;
        }
        //This should drain fluid based on the speed
        if (Math.abs(getSpeed()) > coolantConsumptionTicks){
            coolantConsumptionTicks = 256;
            tank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
        } else {
            coolantConsumptionTicks--;
        }
    }

    public void fireLaser(){
        if (!hasClearance)
            return;


        //Cap heat based on RPM. The boolean is used to determine goggle display
        FluidStack fluids = getFluidStack();
        if (!fluids.isEmpty() && CoolingHandler.heatHandler.containsKey(fluids.getFluid())) {
            noCoolant = false;
            HeatData laserHeat = findHeat();
            float heatCap = Math.min(Math.abs(getSpeed()), CoolingHandler.heatHandler.get(fluids.getFluid()));
            if (laserHeat.getTotalHeat() > heatCap) {
                activeInefficiency = true;
                laserHeat.capHeat(heatCap);
            } else {
                activeInefficiency = false;
            }
            //If heat is too low, break out
            if (laserHeat.getTotalHeat() < 0) {
                heatToLow = true;
                return;
            } else {
                heatToLow = false;
            }

            //Update recent heat (for goggles)
            recentHeat = laserHeat.copyMe();

            LS.updateLaserEmission(
                    recentHeat,
                    (int) (recentHeat.getTotalHeat() + 16),
                    CoolingHandler.efficiencyHandler.get(fluids.getFluid()),
                    level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING));
            LS.tickAffectedEntities();

        } else {
            noCoolant = true;
        }
    }

    @Override
    public Level getLaserWorld() {
        return level;
    }


    //This method looks for all diode heaters within 1 block
    public HeatData findHeat(){
        HeatData track = HeatData.empty();
        for (int x = -1; x != 2; x++){
            for (int y = -1; y != 2; y++){
                for (int z = -1; z != 2; z++){
                    if (x == 0 && y == 0 && z == 0){
                        continue;
                    }
                    track = HeatData.mergeHeats(track, DiodeHeaters.getActiveHeat(level,getBlockPos().offset(x,y,z)));
                }
            }
        }
        return track;
    }

    //This method looks for all diodes within 2 blocks, the area in which they could disrupt diodes clearance. It also invalidates those diodes
    public void testForClearance(){
        hasClearance = true;
        for (int x = -2; x != 3; x++){
            for (int y = -2; y != 3; y++){
                for (int z = -2; z != 3; z++) {
                    BlockPos pos = getBlockPos().offset(x,y,z);
                    if (pos != getBlockPos()){
                        BlockEntity test = level.getBlockEntity(getBlockPos().offset(x, y, z));
                        if (test != null && test.getType() == AllBlockEntities.DIODE.get()) {
                            this.setNoClearance();
                            ((DiodeBlockEntity) test).setNoClearance();
                        }
                    }
                }
            }
        }
    }

    public void wrench() {
        if (!hasClearance) testForClearance();
    }

    public void setNoClearance(){
        hasClearance = false;
    }


    @Override
    public void initialize() {
        super.initialize();
        LS = new LaserSegment(this,recentHeat,this.getBlockPos(),(int)recentHeat.getTotalHeat()+16,level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING),3);
        testForClearance();
    }

    //Data Writing

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        hasClearance = tag.getBoolean("has_clearance");
        activeInefficiency = tag.getBoolean("inefficient");
        heatToLow = tag.getBoolean("heat_too_low");
        coolantConsumptionTicks = tag.getInt("consumption_ticks");
        breakingCounter = tag.getDouble("break_counter");
        timer = tag.getInt("timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("has_clearance",hasClearance);
        tag.putBoolean("inefficient",activeInefficiency);
        tag.putBoolean("heat_too_low",heatToLow);
        tag.putInt("consumption_ticks",coolantConsumptionTicks);
        tag.putDouble("break_counter",breakingCounter);
        tag.putInt("timer",timer);
    }

    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean heatDisplay = true;
        if (!hasClearance){
            tooltip.add(addIndent(Component.translatable("coverheated.diode.needs_clearance_one")));
            tooltip.add(addIndent(Component.translatable("coverheated.diode.needs_clearance_two")));
            heatDisplay = false;

        }
        if (activeInefficiency){
            tooltip.add(addIndent(Component.translatable("coverheated.diode.heat_limited_one")));
            tooltip.add(addIndent(Component.translatable("coverheated.diode.heat_limited_two")));

        }
        if (heatToLow){
            tooltip.add(addIndent(Component.translatable("coverheated.diode.no_heat")));
            heatDisplay = false;
        }
        if (noCoolant){
            tooltip.add(addIndent(Component.translatable("coverheated.diode.no_coolant")));
            heatDisplay = false;
        }
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        GoggleHelper.heatTooltip(tooltip,heatDisplay ? recentHeat : HeatData.empty(), HeatDisplayType.EMIT);
        return true;
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

    //Kinetics

    @Override
    public float calculateStressApplied() {
        float impact = 16f;
        this.lastStressApplied = impact;
        return impact;
    }

}