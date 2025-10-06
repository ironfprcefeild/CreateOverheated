package net.ironf.overheated.laserOptics.Diode;

import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.laserOptics.mirrors.mirrorRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
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


    public void findEntities(){

    }

    //TODO add back damage
    public void tick(){
        Level level = laserSource.getLaserWorld();
        HeatData laserHeat = initialHD;
        Direction continueIn = emissionDirection;
        BlockPos continueAt = origin;

        for (int t = 0; t < range && laserHeat.getTotalHeat() > 0.1; t++) {
            continueAt = continueAt.relative(continueIn);
            BlockState hitState = level.getBlockState(continueAt);

            //Dont do anything if its air besides rendering
            if (!hitState.isAir()) {
                if (!mirrorRegister.isMirror(hitState) && hitState.getBlock().defaultDestroyTime() >= 0) {
                    // Mirror or breakable block.
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


                    //addToDamage(currentOrigin,continueAt);
                    //currentOrigin = continueAt;

                    //We have hit an absorber
                    if (continueIn == null){
                        break;
                    }
                }
            } else {
                //Air, render beam
                markForEffectCloud(continueAt);
                //addToDamage(continueAt.relative(continueIn.getOpposite()),continueAt);
            }
        }
    }


    private void markForEffectCloud(BlockPos continueAt) {
        RandomSource rand = laserSource.getLaserWorld().random;
        double x = continueAt.getX() + rand.nextDouble();
        double y = continueAt.getY() + rand.nextDouble();
        double z = continueAt.getZ() + rand.nextDouble();
        double vx = rand.nextDouble() * 0.04 - 0.02;
        double vy = -0.2;
        double vz = rand.nextDouble() * 0.04 - 0.02;
        laserSource.getLaserWorld().addParticle(ParticleTypes.LAVA, x, y, z, vx, vy, vz);
    }


    //TODO figure out what to do about theese
    public ArrayList<AABB> damageZones = new ArrayList<>();
    private void addToDamage(BlockPos origin, BlockPos ending){
        damageZones.add(new AABB(origin.getX(),origin.getY(),origin.getZ()
                ,ending.getX(),ending.getY(),ending.getZ()));

    }
    private void dealDamage(float volatility) {
        List<Entity> targets = new ArrayList<>();
        damageZones.forEach((bounds) -> targets.addAll(laserSource.getLaserWorld().getEntities(null,bounds)));
        for (Entity entity : targets){
            if (!entity.isAlive()) {
                continue;
            }
            entity.setRemainingFireTicks((int) (volatility * 2));
            entity.hurt(entity.damageSources().lightningBolt(), (float) (volatility * 0.5));
        }


    }

}
