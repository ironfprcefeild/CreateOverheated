package net.ironf.overheated.nuclear.rods;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;



public interface IMakeNeutrinos {
    default void fireNeutrino(int amount, BlockPos from, Direction in, Level level){
        int neutrinos = amount;
        BlockPos currentPos = from;
        int distanceTravelled = amount;
        for (int distance = 1; distance < Math.max(5,amount); distance++) {
            currentPos = currentPos.relative(in);
            neutrinos = ControlRodsRegister.doRegulation(neutrinos,in,level,currentPos,level.getBlockState(currentPos));
            if (neutrinos <= 0){
                distanceTravelled = distance;
                break;
            }

        }

        switch (in.getAxis()){
            case X -> makeParticle(level,from,distanceTravelled/10d*in.getStepX(),0,0);
            case Y -> makeParticle(level,from,0,distanceTravelled/10d*in.getStepY(),0);
            case Z -> makeParticle(level,from,0,0,distanceTravelled/10d*in.getStepZ());
        }
    }

    default void makeParticle(Level level, BlockPos from, double dx, double dy, double dz){
        level.addParticle(ParticleTypes.FLAME, from.getX()+0.5,from.getY()+0.5,from.getZ()+0.5,dx,dy,dz);
    }

    default void fireNeutrinosOrthogonally(int amount, BlockPos from, Level level){
        for (Direction d : Iterate.horizontalDirections){
            fireNeutrino(amount,from,d,level);
        }
    }
}
