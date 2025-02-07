package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

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
    double breakingCounter = 0;

    int timer = 5;
    HeatData recentHeat = HeatData.empty();


    //Laser is updated every tick
    @Override
    public void tick() {
        super.tick();
        if (timer-- <= 0){
            fireLaser();
            hittingLasers = new HeatData[]{HeatData.empty(), HeatData.empty(), HeatData.empty(), HeatData.empty(), HeatData.empty(), HeatData.empty()};
            timer = 5;
        }
        //This should drain fluid based on the speed
        if (Math.abs(getSpeed()) > coolantConsumptionTicks){
            coolantConsumptionTicks = 258;
            tank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
        } else {
            coolantConsumptionTicks--;
        }
        dealDamage(recentHeat.getTotalHeat());
    }

    public void fireLaser(){
        if (!hasClearance)
            return;


        //Cap heat based on RPM. The boolean is used to determine goggle display
        FluidStack fluids = getFluidStack();
        if (!fluids.isEmpty() && CoolingHandler.heatHandler.containsKey(fluids.getFluid())) {
            noCoolant = false;
            float currentCoolantEff = CoolingHandler.efficiencyHandler.get(fluids.getFluid());
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

            int range = (int) Math.ceil(laserHeat.getTotalHeat()*(currentCoolantEff));

            //Update recent heat
            recentHeat = laserHeat.copyMe();
            //Propagate Laser
            //32 Limits the lasers length, it's also limited by the heat of the laser
            Direction continueIn = getBlockState().getValue(BlockStateProperties.FACING);
            BlockPos continueAt = getBlockPos();
            BlockPos currentOrigin = continueAt.relative(continueIn);
            for (int t = 0; t < Math.min(32, range) + 16; t++) {
                if (laserHeat.getTotalHeat() < 0.1) {
                    //Laser isout of heat, so we gotta jumpy away
                    break;
                }
                continueAt = continueAt.relative(continueIn);
                BlockState hitState = level.getBlockState(continueAt);
                continueIn = mirrorRegister.doReflection(continueIn, level, continueAt, hitState,laserHeat);
                //Dont do anything if its air besides rendering
                if (!hitState.isAir()) {
                    if (AllBlocks.ANTI_LASER_PLATING.has(hitState) || Blocks.BEDROCK == hitState.getBlock()) {
                        //Anti laser plating or bedrock, cant be destroyed, so we just break here
                        break;
                    } else if (!mirrorRegister.isMirror(hitState)) {
                        //Dont do anything if a mirror, otherwise check for laser absorbers
                        BlockEntity hitBE = level.getBlockEntity(continueAt);
                        if (hitBE instanceof ILaserAbsorber) {
                            if (!((ILaserAbsorber) hitBE).absorbLaser(continueIn, laserHeat,range-t,currentCoolantEff)) {
                                //This is letting the laser continue if absorb laser tells us too, otherwise we break
                                break;
                            }
                        } else {
                            //This isnt a laser absorber or a mirror, so we can do normal block stuff
                            //We are at a normal block, so lets break it!
                            breakingCounter = (breakingCounter + (laserHeat.getTotalHeat()*currentCoolantEff));
                            double counterNeeded = hitState.getBlock().defaultDestroyTime() * 7.5;
                            if (counterNeeded < breakingCounter) {
                                level.destroyBlock(continueAt,true);
                                breakingCounter = breakingCounter - counterNeeded;
                            }
                            break;
                        }
                    } else {
                        //We are at a mirror, cause damage and update origin
                        addToDamage(currentOrigin,continueAt);
                        currentOrigin = continueAt;
                    }
                } else {
                    //Render the little beam
                    markForEffectCloud(continueAt);
                }
            }
            //We are done with the laser, cause damage
            addToDamage(currentOrigin,continueAt);
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

    //public static final Holder<DamageType> Ions = Holder.direct(new DamageType("ions", DamageScaling.NEVER,2f));
    public ArrayList<AABB> damageZones = new ArrayList<>();
    private void addToDamage(BlockPos origin, BlockPos ending){
        damageZones.add(new AABB(origin,ending));
        damageZones.add(new AABB(origin.getX()+1,origin.getY()+1,origin.getZ()+1
                                ,ending.getX()-1,ending.getY()-1,ending.getZ()-1));

    }
    private void dealDamage(float volatility) {
        List<Entity> targets = new ArrayList<>();
        damageZones.forEach((bounds) -> targets.addAll(level.getEntities(null,bounds)));
        for (Entity entity : targets){
            if (!entity.isAlive()) {
                continue;
            }
            entity.setRemainingFireTicks((int) (volatility * 2));
            entity.hurt(entity.damageSources().lightningBolt(), (float) (volatility * 0.1));
        }


    }


    public HeatData[] hittingLasers = {HeatData.empty(),HeatData.empty(),HeatData.empty(),HeatData.empty(),HeatData.empty(),HeatData.empty()};
    public static Map<Direction, Integer> inputHelper = Map.of(Direction.UP,0,Direction.DOWN,1,Direction.NORTH,2,Direction.SOUTH,3,Direction.EAST,4,Direction.WEST,5);
    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat, int d, float eff) {
        if (getBlockState().getValue(BlockStateProperties.FACING) != incoming.getOpposite()) {
            hittingLasers[inputHelper.get(incoming)] = beamHeat;
        }
        return false;
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

    public void wrench() {
        if (!hasClearance) testForClearance();
    }

    public void setNoClearance(){
        hasClearance = false;
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
        activeInefficiency = tag.getBoolean("inefficient");
        heatToLow = tag.getBoolean("heat_too_low");
        coolantConsumptionTicks = tag.getInt("consumption_ticks");
        breakingCounter = tag.getDouble("break_counter");
        timer = tag.getInt("timer");
        hittingLasers = HeatData.readHeatDataArray(tag,"incoming_heat");
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
        HeatData.writeHeadDataArray(tag,hittingLasers,"incoming_heat");
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
