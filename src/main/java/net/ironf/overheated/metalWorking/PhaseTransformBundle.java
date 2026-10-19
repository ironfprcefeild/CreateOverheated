package net.ironf.overheated.metalWorking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhaseTransformBundle {
    public final List<Float> criticalPoints;
    public final List<UniversalState> createdStates;
    public final boolean decayingPhase;
    public PhaseTransformBundle(List<Float> criticalPoints, List<UniversalState> createdStates) {
        this.criticalPoints = criticalPoints;
        this.createdStates = createdStates;
        decayingPhase = false;
    }

    public PhaseTransformBundle(UniversalState decayState, boolean decayingPhase) {
        this.criticalPoints = new ArrayList<>();
        criticalPoints.add(0f);
        this.createdStates = new ArrayList<>();
        createdStates.add(decayState);
        this.decayingPhase = decayingPhase;
    }

    public PhaseTransformBundle(List<Float> criticalPoints, NonNullList<UniversalState> phases) {
        this.criticalPoints = criticalPoints;
        this.createdStates = new ArrayList<>();
        createdStates.addAll(phases);
        decayingPhase = false;
    }

    public void executeTransformation(Float temp, BlockPos at, Level level){
        if (decayingPhase){
            createdStates.getFirst().set(level,at,3,true);
        } else {
            UniversalState transitionState = transformationFromTemp(temp);
            if (transitionState != UniversalState.stateAt(level, at)) {
                transitionState.set(level, at, 3);
            }
        }
    }

    public UniversalState transformationFromTemp(Float temp){
        for (int i = 0; i < criticalPoints.size(); i++) {
            if (temp <= criticalPoints.get(i)){
                return createdStates.get(i);
            }
        }
        return createdStates.getFirst();
    }
}
