package net.ironf.overheated.metalWorking;

import net.minecraft.core.NonNullList;

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

    public UniversalState transformationFromTemp(Float temp){
        for (int i = 0; i < criticalPoints.size(); i++) {
            if (temp <= criticalPoints.get(i)){
                return createdStates.get(i);
            }
        }
        if (decayingPhase){
            return createdStates.getFirst();
        } else {
            return createdStates.getLast();
        }
    }
}
