package net.ironf.overheated.laserOptics.Diode;

import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class LaserSegment {


    public final ILaserEmitter laserSource;
    public double breakingCounter;
    public int range;
    public HeatData initialHD;
    public BlockPos origin;
    public Direction emissionDirection;
    public float volatility;

    public LaserSegment(ILaserEmitter laserSource, HeatData initialHD, BlockPos origin, int Range, Direction direction, float volatility) {
        this.laserSource = laserSource;


        this.breakingCounter = 0;
        this.initialHD = initialHD;
        this.origin = origin;
        this.range = Range;
        this.emissionDirection = direction;
        this.volatility =volatility;
    }

    public void updateLaserEmission(HeatData hd, int range, float volatility, Direction direction){
        this.initialHD = hd;
        this.range = range;
        this.emissionDirection = direction;
        this.volatility = volatility;
        tick();
    }

    public ArrayList<AABB> damageZones = new ArrayList<>();

    public void tickAffectedEntities(){
        List<Entity> caughtEntities;
        Level world = laserSource.getLaserWorld();
        int fireTicks = (int) ((volatility)*(initialHD.getTotalHeat()));
        int damage = (int) ((volatility)*(initialHD.getTotalHeat())*(0.4));
        for (AABB zone : damageZones) {
            caughtEntities = (world.getEntities(null, zone));
            for (Entity entity : caughtEntities) {
                if (!entity.isAlive() || !entity.getBoundingBox().intersects(zone)) {
                    continue;
                }
                entity.setRemainingFireTicks(fireTicks);
                entity.hurt(entity.damageSources().lightningBolt(),damage);
            }
        }
    }

    public void tick(){
        Level level = laserSource.getLaserWorld();
        HeatData laserHeat = initialHD;
        Direction continueIn = emissionDirection;
        BlockPos continueAt = origin;
        damageZones.clear();
        BlockPos lastBound = origin;
        for (int t = 0; t < range && laserHeat.getTotalHeat() > 0.1; t++) {
            continueAt = continueAt.relative(continueIn);
            BlockState hitState = level.getBlockState(continueAt);

            //Dont do anything if its air besides rendering
            if (!hitState.isAir()) {
                if (!mirrorRegister.isMirror(hitState) && hitState.getBlock().defaultDestroyTime() >= 0) {
                    //Non-Mirror or breakable block.
                    breakingCounter = (breakingCounter + (laserHeat.getTotalHeat()*volatility));
                    double counterNeeded = hitState.getBlock().defaultDestroyTime() * 7.5;
                    if (counterNeeded < breakingCounter) {
                        //break, lower counter, and pass to test for additional break
                        level.destroyBlock(continueAt,true);
                        breakingCounter = breakingCounter - counterNeeded;
                        continue;
                    }
                    break;

                } else {
                    //Mirror or unbreakable block

                    //Set this to 0 to avoid a buildup of breaking power
                    breakingCounter = 0;
                    continueIn = mirrorRegister.doReflection(continueIn, level, continueAt, hitState,laserHeat);

                    //Damage Zone
                    damageZones.add(damageZoneAsField(continueAt,lastBound));
                    lastBound = continueAt;

                    //We have hit an absorber
                    if (continueIn == null){
                        break;
                    }
                }
            } else {
                //Air, render beam
                markForEffectCloud(continueAt);
                markForEffectCloud(continueAt);
            }
        }
        damageZones.add(damageZoneAsField(continueAt,lastBound));
    }

    private void markForEffectCloud(BlockPos continueAt) {
        RandomSource rand = laserSource.getLaserWorld().random;
        double x = continueAt.getX() + rand.nextDouble();
        double y = continueAt.getY() + rand.nextDouble();
        double z = continueAt.getZ() + rand.nextDouble();
        double vx = rand.nextDouble() * 0.04 - 0.02;
        double vy = -0.2;
        double vz = rand.nextDouble() * 0.04 - 0.02;
        laserSource.getLaserWorld().addParticle(ParticleTypes.FLAME, x, y, z, vx, vy, vz);
    }

    public static AABB damageZoneAsField(BlockPos continueAt, BlockPos lastBound){
        return new AABB(continueAt.getX(),continueAt.getY(),continueAt.getZ(),lastBound.getX()+1,lastBound.getY()+1,lastBound.getZ()+1);
    }

}
