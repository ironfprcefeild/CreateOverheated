package net.ironf.overheated.laserOptics.mirrors.splitMirror;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SplitMirrorBlockEntity extends SmartBlockEntity implements ILaserAbsorber {
    public SplitMirrorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    @Override
    public boolean absorbLaser(Direction incoming, HeatData beamHeat, int distance) {
        Direction imFacing = getBlockState().getValue(SplitMirrorBlock.FACING);
        //Laser not coming through input
        if (imFacing != incoming.getOpposite()) {
            return false;
        }
        HeatData Heat = new HeatData(beamHeat.Heat / 2, beamHeat.SuperHeat / 2, beamHeat.OverHeat / 2, beamHeat.Volatility / 2);
        fireLaser(Heat.copyMe(), Direction.UP, 0, distance / 2);
        fireLaser(Heat.copyMe(), Direction.DOWN, 1, distance / 2);

        return false;
    }

    public double[] breakingCounters = {0,0};

    //Assumes all conditions of the diode are met
    public void fireLaser(HeatData laserHeat, Direction initialDirection, int usedCounter, int distancePenalty){
        //If heat is too low, break out
        if (laserHeat.getTotalHeat() < 0)
            return;

        int range = (int) Math.ceil(laserHeat.Volatility + laserHeat.getTotalHeat());
        //Propogate Laser
        //32 Limits the lasers length, its also limited by the heat of the laser
        Direction continueIn = initialDirection;
        BlockPos continueAt = getBlockPos();
        BlockPos currentOrigin = continueAt.relative(continueIn);
        for (int t = distancePenalty; t < Math.min(32, range) + 16; t++) {
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
                        if (!((ILaserAbsorber) hitBE).absorbLaser(continueIn, laserHeat,t)) {
                            //This is letting the laser continue if absorb laser tells us too, otherwise we break
                            break;
                        }
                    } else {
                        //This isnt a laser absorber or a mirror, so we can do normal block stuff
                        //We are at a normal block, so lets break it!
                        breakingCounters[usedCounter] = ( breakingCounters[usedCounter] + Math.min(laserHeat.Volatility, laserHeat.getTotalHeat()));
                        double counterNeeded = hitState.getBlock().defaultDestroyTime() * 7.5;
                        if (counterNeeded <  breakingCounters[usedCounter]) {
                            level.destroyBlock(continueAt,true);
                            breakingCounters[usedCounter] =  (breakingCounters[usedCounter] - counterNeeded);
                        }
                        break;
                    }
                } else {
                    //We are at a mirror, cause damage and update origin
                    dealDamage(currentOrigin,continueAt,laserHeat.Volatility);
                    currentOrigin = continueAt;
                }
            } else {
                //Render the little beam
                markForEffectCloud(continueAt);
            }
            //We are done with the laser, cause damage
            dealDamage(currentOrigin,continueAt,laserHeat.Volatility);
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
    private void dealDamage(BlockPos origin, BlockPos Ending, float volatility) {
        /*
        AABB bounds = new AABB(origin,Ending);
        List<Entity> targets = level.getEntities(null,bounds);
        for (Entity entity : targets){
            if (!entity.isAlive() || !entity.getBoundingBox().intersects(bounds)) {
                continue;
            }
            entity.setRemainingFireTicks((int) (volatility * 2));
            entity.hurt(new DamageSource(Ions, getBlockPos().getCenter()), (float) (volatility * 0.5));
        }

         */
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        breakingCounters[0] = tag.getDouble("counterzero");
        breakingCounters[1] = tag.getDouble("counterone");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putDouble("counterzero",breakingCounters[0]);
        tag.putDouble("counterone", breakingCounters[1]);
    }
}
