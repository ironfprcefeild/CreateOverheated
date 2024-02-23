package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.colants.LaserCoolingHandler;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
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
import java.util.Map;

import static net.ironf.overheated.Overheated.lang;

public class DiodeBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IWrenchable, ILaserAbsorber {
    public DiodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(5);
    }


    //Doing stuff
    public boolean hasClearance = false;
    boolean activeInefficiency = false;
    boolean heatToLow = false;
    boolean noCoolant = false;
    int coolantConsumptionTicks = 258;
    int breakingCounter = 0;

    HeatData recentHeat = HeatData.empty();


    //Laser is updated every tick
    @Override
    public void tick() {
        super.tick();
        //This should drain fluid based on the speed
        if (Math.abs(getSpeed()) > coolantConsumptionTicks){
            coolantConsumptionTicks = 258;
            tank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
        } else {
            coolantConsumptionTicks--;
        }

        //Lower and reset timers for hitting lasers
        for (int i = 0; i != 6; i++){
            int t = hittingTimers[i];
            if (t != 0){
                t--;
                if (t == 0){
                    hittingLasers[i] = HeatData.empty();
                }
            }
        }
    }


    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!hasClearance)
            return;


        //Cap heat based on RPM. The boolean is used to determine goggle display
        FluidStack fluids = getFluidStack();
        if (!fluids.isEmpty() && LaserCoolingHandler.heatHandler.containsKey(fluids.getFluid())) {
            noCoolant = false;
            HeatData laserHeat = findHeat();
            int heatCap = (int) Math.min(Math.abs(getSpeed()), LaserCoolingHandler.heatHandler.get(fluids.getFluid()));
            if (laserHeat.getTotalHeat() > heatCap) {
                activeInefficiency = true;
                while (laserHeat.getTotalHeat() > heatCap) {
                    laserHeat.useUpToOverHeat();
                }
            } else {
                activeInefficiency = false;
            }
            //If heat is too low, break out
            if (laserHeat.getTotalHeat() < 1) {
                heatToLow = true;
                return;
            } else {
                heatToLow = false;
            }
            recentHeat = laserHeat;
            //Set Volatility
            laserHeat.Volatility = LaserCoolingHandler.volatilityHandler.get(fluids.getFluid());
            int range = laserHeat.Volatility + laserHeat.getTotalHeat();
            //Propogate Laser
            //16 Limits the lasers length, its also limited by the heat of the laser
            Direction continueIn = getBlockState().getValue(BlockStateProperties.FACING);
            BlockPos continueAt = getBlockPos();
            for (int t = 0; t < Math.min(16, range) + 16; t++) {
                if (laserHeat.getTotalHeat() < 1) {
                    //Laser isout of heat, so we gotta jumpy away
                    break;
                }
                continueAt = continueAt.relative(continueIn);
                BlockState hitState = level.getBlockState(continueAt);
                continueIn = mirrorRegister.doReflection(continueIn, level, continueAt, hitState,laserHeat);
                addEffectCloud(continueAt);
                //Dont do anything if its air besides rendering
                if (!hitState.isAir()) {
                    if (AllBlocks.ANTI_LASER_PLATING.has(hitState) || Blocks.BEDROCK == hitState.getBlock()) {
                        //Anti laser plating or bedrock, cant be destroyed, so we just break here
                        break;
                    } else if (!mirrorRegister.isMirror(hitState)) {
                        //Dont do anything if a mirror, otherwise check for laser absorbers
                        BlockEntity hitBE = level.getBlockEntity(continueAt);
                        if (hitBE instanceof ILaserAbsorber) {
                            if (!((ILaserAbsorber) hitBE).absorbLaser(continueIn, laserHeat)) {
                                //This is letting the laser contiune if absorb laser tells us too, otherwise we break
                                break;
                            }
                        } else {
                            //This isnt a laser absorber or a mirror, so we can do normal block stuff
                            //We are at a normal block, so lets break it!
                            breakingCounter = breakingCounter + Math.min(laserHeat.Volatility, laserHeat.getTotalHeat());
                            if (canBreak(hitState)) {
                                level.destroyBlock(continueAt,true);
                                breakingCounter = 0;
                            }
                            break;
                        }
                    }
                } else {
                    //Render the little beam
                    markForEffectCloud(continueAt);
                }
            }
        } else {
            noCoolant = true;
        }
    }

    private void markForEffectCloud(BlockPos continueAt) {
        RandomSource rand = level.random;
        double x = continueAt.getX() + rand.nextDouble();
        double y = continueAt.getY() + rand.nextDouble();
        double z = continueAt.getZ() + rand.nextDouble();
        double vx = rand.nextDouble() * 0.04 - 0.02;
        double vy = -0.2;
        double vz = rand.nextDouble() * 0.04 - 0.02;
        level.addParticle(ParticleTypes.LAVA, x, y, z, vx, vy, vz);
    }




    public boolean canBreak(BlockState hitState){
        return (hitState.getBlock().defaultDestroyTime() * 2.5) < breakingCounter;

    }

    public HeatData[] hittingLasers = {HeatData.empty(),HeatData.empty(),HeatData.empty(),HeatData.empty(),HeatData.empty(),HeatData.empty()};
    public int[] hittingTimers = {0,0,0,0,0,0};
    public static Map<Direction, Integer> inputHelper = Map.of(Direction.UP,0,Direction.DOWN,1,Direction.NORTH,2,Direction.SOUTH,3,Direction.EAST,4,Direction.WEST,5);
    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat) {
        ILaserAbsorber.super.absorbLaser(incoming,beamHeat);
        if (getBlockState().getValue(BlockStateProperties.FACING) != incoming.getOpposite()) {
            hittingLasers[inputHelper.get(incoming)] = beamHeat;
            hittingTimers[inputHelper.get(incoming)] = 6;
        }
        return false;
    }

    private void addEffectCloud(BlockPos continueAt) {

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

        track.Volatility = 0;
        track = HeatData.mergeHeats(HeatData.mergeHeats(hittingLasers),track);
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

    public void setNoClearance(){
        hasClearance = false;
    }


    @Override
    public void initialize() {
        super.initialize();
        testForClearance();
        setLazyTickRate(5);
    }

    //Data Writing

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        hasClearance = tag.getBoolean("has_clearance");
        activeInefficiency = tag.getBoolean("inefficient");
        heatToLow = tag.getBoolean("heat_too_low");
        coolantConsumptionTicks = tag.getInt("consumption_ticks");
        breakingCounter = tag.getInt("break_counter");
        hittingTimers = tag.getIntArray("hitting_timers");
        hittingLasers = HeatData.readHeatDataArray(tag,"incoming_heat");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("has_clearance",hasClearance);
        tag.putBoolean("inefficient",activeInefficiency);
        tag.putBoolean("heat_too_low",heatToLow);
        tag.putInt("consumption_ticks",coolantConsumptionTicks);
        tag.putInt("break_counter",breakingCounter);
        tag.putIntArray("hitting_timers",hittingTimers);
        HeatData.writeHeadDataArray(tag,hittingLasers,"incoming_heat");
    }

    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (!hasClearance){
            tooltip.add(Component.translatable("coverheated.diode.needs_clearance_one"));
            tooltip.add(Component.translatable("coverheated.diode.needs_clearance_two"));
        }
        if (activeInefficiency){
            tooltip.add(Component.translatable("coverheated.diode.heat_limited"));
        }
        if (heatToLow){
            tooltip.add(Component.translatable("coverheated.diode.no_heat"));
        }
        if (noCoolant){
            tooltip.add(Component.translatable("coverheated.diode.no_coolant"));
        }
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        if (recentHeat != HeatData.empty()){
            tooltip.add(Component.literal(" "));
            tooltip.add(Component.translatable("coverheated.heat").append(String.valueOf(recentHeat.Heat)));
            tooltip.add(Component.translatable("coverheated.superheat").append(String.valueOf(recentHeat.SuperHeat)));
            tooltip.add(Component.translatable("coverheated.overheat").append(String.valueOf(recentHeat.OverHeat)));
            tooltip.add(Component.translatable("coverheated.laser_power").append(String.valueOf(Math.min(recentHeat.Volatility, recentHeat.getTotalHeat()))));
        } else {
            tooltip.add(Component.translatable("coverheated.no_heat"));
        }


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
        float impact = 4f;
        this.lastStressApplied = impact;
        return impact;
    }
}
